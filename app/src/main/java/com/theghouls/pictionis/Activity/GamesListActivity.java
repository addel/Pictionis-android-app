package com.theghouls.pictionis.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Model.Player;
import com.theghouls.pictionis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GamesListActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_channel = new ArrayList<>();

    // Firebase database ref
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference root = database.getReference().child("Games_list");


    private String username;
    private Player player;
    private boolean isMulti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(addGamesListener);

        username = getIntent().getExtras().get("username").toString();

        ListView listView = (ListView) findViewById(R.id.game_list);
        listView.setOnItemClickListener(listViewItemListener);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_channel);
        listView.setAdapter(arrayAdapter);

        root.addValueEventListener(rootValuesListener);

    }


    ////////////////////////
    ///// LISTENER /////////
    ///////////////////////

    private View.OnClickListener addGamesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            game_name_alert();
        }
    };

    private AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference rootGame = database.getReference().child("Games_list").child(((TextView) view).getText().toString()).child("isMulti");
            rootGame.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isMulti = (boolean) dataSnapshot.getValue();

                    Intent intent;
                    if(isMulti){
                        intent = new Intent(GamesListActivity.this, GameTeamBuildingActivity.class);
                    }else{
                        intent = new Intent(GamesListActivity.this, GameActivity.class);
                    }

                    intent.putExtra("username", username);
                    intent.putExtra("game_names", ((TextView) view).getText().toString());
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    };

    private ValueEventListener rootValuesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Set<String> set = new HashSet<String>();
            Iterator i =dataSnapshot.getChildren().iterator();

            while(i.hasNext()){
                set.add(((DataSnapshot) i.next()).getKey());
            }
            list_channel.clear();
            list_channel.addAll(set);

            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    ///////////////////
    /// HELPER ////////
    ///////////////////

    private void game_name_alert(){

        final EditText edittext = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        edittext.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Veullez entrez un nom de partie ")
                .setTitle("Nom de la partie");
        builder.setIcon(R.drawable.ic_pictionislogo);

        builder.setView(edittext);

        builder.setPositiveButton("Un joueur", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String nom = edittext.getText().toString().trim();

                Map <String, Object> map = new HashMap<String, Object>();
                Map <String, Object> map2 = new HashMap<String, Object>();
                Map <String, Object> players = new HashMap<String, Object>();
                Map <String, Object> multi = new HashMap<String, Object>();
                player = new Player(username, 1, true);
                map2.put(username, player);
                players.put("players", map2);
                map.put(nom, players);
                multi.put("isMulti", false);
                root.updateChildren(map);
                root.child(nom).updateChildren(multi);

            }
        });

        builder.setNegativeButton("Annulez", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Multi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String nom = edittext.getText().toString().trim();

                Map <String, Object> map = new HashMap<String, Object>();
                Map <String, Object> map2 = new HashMap<String, Object>();
                Map <String, Object> players = new HashMap<String, Object>();
                Map <String, Object> multi = new HashMap<String, Object>();
                player = new Player(username, 1, true);
                map2.put(username, player);
                players.put("players", map2);
                multi.put("isMulti", true);
                map.put(nom, players);
                root.updateChildren(map);
                root.child(nom).updateChildren(multi);
            }
        });

        builder.show();

    }
}
