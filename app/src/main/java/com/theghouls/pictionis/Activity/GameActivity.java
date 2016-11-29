package com.theghouls.pictionis.Activity;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Model.Player;
import com.theghouls.pictionis.R;
import com.theghouls.pictionis.View.ChatFragment;
import com.theghouls.pictionis.View.DrawingFragment;
import com.theghouls.pictionis.View.MyViewPager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

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

        username = getIntent().getExtras().get("username").toString();
        String game_name = getIntent().getExtras().get("game_names").toString();

        root_game_player = database.getReference().child("Games_list").child(game_name).child("players");
        root_game_player.addValueEventListener(root_gamePlayerListener);

        root_game = database.getReference().child("Games_list").child(game_name);
        root_game.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.child("players").getChildren()){
                    if (snapshot.getValue(Player.class).getName().equals(username) && snapshot.getValue(Player.class).isAdmin()){
                        isAdmin = true;
                    }else{
                        isAdmin = false;
                    }
                }

                word = (String) dataSnapshot.child("word").getValue();

                if(word == null && isAdmin){
                    userChooseWord();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewPager = (MyViewPager) findViewById(R.id.viewpager);
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

    private ValueEventListener root_gamePlayerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            List<String> listPlayerName = new ArrayList<>();

            for(DataSnapshot snap: dataSnapshot.getChildren()){
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

    public void userChooseWord(){

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

    public void checkword(String wd){
        final Collator instance = Collator.getInstance();

        // This strategy mean it'll ignore the accents
        instance.setStrength(Collator.NO_DECOMPOSITION);

        if(instance.compare(wd.toLowerCase(), word.toLowerCase()) == 0){
            HashMap<String, Object> map = new HashMap<>();
            map.put("win", true);
            root_game.updateChildren(map);
        }
    }

    private void registerWordAndPoint(String word_r, int point){
        Map <String, Object> map = new HashMap<String, Object>();
        word = word_r;
        map.put("word", word_r);
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

    //////////////////////////
    //// Fragment + pager ////
    //////////////////////////

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putBoolean("isadmin", isAdmin);
        bundle.putString("username", username);
        DrawingFragment fragobj = new DrawingFragment();
        fragobj.setArguments(bundle);

        Bundle bundleChat = new Bundle();
        bundleChat.putString("username", username);
        ChatFragment fragobjChat = new ChatFragment();
        fragobjChat.setArguments(bundleChat);



        adapter.addFragment(fragobj, "DESSIN");
        adapter.addFragment(fragobjChat, "CHAT");
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
