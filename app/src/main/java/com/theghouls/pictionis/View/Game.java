package com.theghouls.pictionis.View;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Model.Player;
import com.theghouls.pictionis.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game extends AppCompatActivity {

    private EditText txtField_response;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String username;

    private String word = "";
    private boolean win;
    private boolean isAdmin = false;
    private int pointToWin;
    private List<String> listOfWord = new ArrayList<>();


    private Set<Player> setPlayer = new HashSet<Player>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference root_game_player;
    private DatabaseReference root_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        listOfWord.clear();

        txtField_response = (EditText)findViewById(R.id.txtField_response);

        username = getIntent().getExtras().get("username").toString();
        String game_name = getIntent().getExtras().get("game_names").toString();

        root_game_player = database.getReference().child("Games_list").child(game_name).child("players");
        root_game_player.addValueEventListener(root_gameListener);

        root_game = database.getReference().child("Games_list").child(game_name);
        root_game.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("word").getValue() == null){
                    userChooseWord();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    ////////////////////
    ///// LISTENER ////
    ///////////////////

    private ValueEventListener root_gameListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            List<String> listPlayerName = new ArrayList<>();

            for(DataSnapshot snap: dataSnapshot.getChildren()){
                String name = snap.getValue(Player.class).getName();
                boolean isAdminCheck = snap.getValue(Player.class).isAdmin();
                if (snap.getValue(Player.class).getName().equals(username) && snap.getValue(Player.class).isAdmin()){
                    isAdmin = true;
                }else{
                    //changeEditText(txtField_response, false);
                }
                setPlayer.add(snap.getValue(Player.class));
                listPlayerName.add(snap.getValue(Player.class).getName());
            }

            if(!listPlayerName.contains(username)){
                Player p = new Player();
                p.setTeam(1);
                p.setAdmin(false);
                p.setName(username);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(username, p);
                root_game_player.updateChildren(map);
                listPlayerName.add(username);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    ///////////////////
    //// HELPER //////
    /////////////////

    public DatabaseReference getReferenceGame(){
        return root_game;
    }

    private void userChooseWord(){

        final String easyWord = generateWord("easyWord.txt");
        final String mediumWord = generateWord("mediumWord.txt");
        final String hardWord = generateWord("hardWord.txt");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Veuillez choisir un mot à faire devinez");
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark_normal);

        builder.setPositiveButton(easyWord + " (10 point)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                registerWordAndPoint(easyWord, 10);
            }
        });

        builder.setNegativeButton(mediumWord + " (20 point)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                registerWordAndPoint(mediumWord, 20);
            }
        });
        builder.setNeutralButton(hardWord + " (50 point)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                registerWordAndPoint(hardWord, 50);
            }
        });

        builder.show();
    }

    private void registerWordAndPoint(String word, int point){
        Map <String, Object> map = new HashMap<String, Object>();
        map.put("word", word);
        root_game.updateChildren(map);
        map.clear();
        map.put("pointForWord", point);
        root_game.updateChildren(map);
    }

    private List<String> getListOfWord(String levelFile){
        listOfWord.clear();
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(getAssets().open(levelFile)));
            String line;
            while ((line = buf.readLine()) != null){
                listOfWord.add(line);
            }
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listOfWord;
    }

    private String generateWord(String levelFile){
        listOfWord = getListOfWord(levelFile);
        int rand = (int) (Math.floor(Math.random() * listOfWord.size()));
        return listOfWord.get(rand).trim();

    }

    private void changeEditText(EditText editText, boolean isActive){
        editText.setEnabled(isActive);
        editText.setCursorVisible(isActive);
    }

    //////////////////////////
    //// Fragment + pager ////
    //////////////////////////

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DrawingFragment(), "DESSIN");
        adapter.addFragment(new ChatFragment(), "CHAT");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
