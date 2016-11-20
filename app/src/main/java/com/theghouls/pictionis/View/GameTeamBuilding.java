package com.theghouls.pictionis.View;

import android.content.DialogInterface;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Model.Player;
import com.theghouls.pictionis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameTeamBuilding extends AppCompatActivity {

    private ArrayList<Player> active_list_player;
    // Firebase database ref
    private DatabaseReference cursor, cursor_team;
    private EditText txtField_message_game;
    private TextView txtView_message_game, team1_txtfield, team2_txtfield;

    private String active_username, active_name_game, temp_key;
    private String chat_message_game, chat_username_game;

    private Player active_player = new Player();
    private ArrayList<Player> listOfPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_team_building);



        active_username = getIntent().getExtras().get("username").toString();
        active_name_game = getIntent().getExtras().get("game_names").toString();

        cursor = FirebaseDatabase.getInstance().getReference().child("Games_list").child(active_name_game).child("chat");
        cursor.addChildEventListener(cursor_childListener);

        cursor_team = FirebaseDatabase.getInstance().getReference().child("Games_list").child(active_name_game).child("players");
        cursor_team.addChildEventListener(cursor_child_teamListener);

        cursor_team.child(active_username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                active_player = dataSnapshot.getValue(Player.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // active_player = (Player)cursor_team.child(active_username).getRef() != null ? (Player)getIntent().getExtras().getSerializable("player") : new Player(active_username, 2, false);;


        // set titre de la view
        setTitle("Partie: "+ active_name_game);


        Button btn_send_game = (Button) findViewById(R.id.btn_send_game);
        Button btn_join_team = (Button) findViewById(R.id.join_game_btn);
        Button btn_lauch_game = (Button) findViewById(R.id.start_game_btn);

        team1_txtfield = (TextView)findViewById(R.id.team1_txtfield);
        team2_txtfield = (TextView)findViewById(R.id.team2_txtfield);

        txtField_message_game = (EditText)findViewById(R.id.txtField_message_game);
        txtView_message_game = (TextView) findViewById(R.id.txtView_chat_game);


        btn_send_game.setOnClickListener(btn_sendListener);
        btn_join_team.setOnClickListener(btn_join_teamListener);
        btn_lauch_game.setOnClickListener(btn_lauch_gameListener);

    }

    private ChildEventListener cursor_childListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Iterator i =dataSnapshot.getChildren().iterator();

            while(i.hasNext()){
                chat_message_game = (String)(((DataSnapshot) i.next()).getValue());
                chat_username_game = (String)(((DataSnapshot) i.next()).getValue());

                txtView_message_game.append(chat_username_game + ":" + chat_message_game + "\n");
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ChildEventListener cursor_child_teamListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            String tmp = team1_txtfield.getText().toString().replace(active_username +  "\n", "");
            String tmp2 = team2_txtfield.getText().toString().replace(active_username +  "\n", "");

            team1_txtfield.setText(tmp);
            team2_txtfield.setText(tmp2);

            if (String.valueOf(dataSnapshot.child("team").getValue()) == "1"){

                team1_txtfield.append(active_username +  "\n");
            }else{
                team2_txtfield.append(active_username +  "\n");
            }


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String tmp = team1_txtfield.getText().toString().replace(active_username +  "\n", "");
            String tmp2 = team2_txtfield.getText().toString().replace(active_username +  "\n", "");

            team1_txtfield.setText(tmp);
            team2_txtfield.setText(tmp2);

            if (String.valueOf(dataSnapshot.child("team").getValue()) == "1"){

                team1_txtfield.append(active_username +  "\n");
            }else{
                team2_txtfield.append(active_username +  "\n");
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener btn_sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            temp_key = cursor.push().getKey();

            // on creer une deuxieme reference (ce qu'on vient tout juste de creer )
            DatabaseReference message_cursor = cursor.child(temp_key);
            Map<String, Object> mapValue  = new HashMap<String, Object>();

            // populate
            mapValue.put("name", active_username);
            mapValue.put("message", txtField_message_game.getText().toString());

            message_cursor.updateChildren(mapValue);

            txtField_message_game.getText().clear();
            txtField_message_game.requestFocus();

        }
    };

    private View.OnClickListener btn_join_teamListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            game_select_team();

        }
    };

    private View.OnClickListener btn_lauch_gameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(active_player.isAdmin()){
                int numberTeam1 = team1_txtfield.getText().toString().split("\n").length;
                int numberTeam2 = team2_txtfield.getText().toString().split("\n").length;

                if(numberTeam1 < 2 || numberTeam2 < 2){
                    Toast.makeText(getApplicationContext(), "Faites moi des fucking team fair", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "C'est parti pour la game de merde", Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(getApplicationContext(), "Vous devez etre admin de la parti pour lancez la game", Toast.LENGTH_LONG).show();
            }

        }
    };

    ///////////////////
    /// HELPER ////////
    ///////////////////

    private void game_select_team(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Equipe");
        builder.setIcon(R.drawable.ic_pictionislogo);

        builder.setItems(R.array.team, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Item", String.valueOf((which)));

                Map <String, Object> map = new HashMap<String, Object>();
                Map <String, Object> map2 = new HashMap<String, Object>();
                Map <String, Object> players = new HashMap<String, Object>();
                active_player.setTeam(which + 1);
                map2.put(active_username, active_player);
                cursor_team.updateChildren(map2);
            }
        });


        AlertDialog alert = builder.create();

        alert.show();

    }
}
