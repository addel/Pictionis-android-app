package com.theghouls.pictionis.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theghouls.pictionis.Model.Player;
import com.theghouls.pictionis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameTeamBuilding extends AppCompatActivity {

    private ArrayList<Player> active_list_player;
    // Firebase database ref
    private DatabaseReference cursor;
    private Button btn_send_game;
    private EditText txtField_message_game;
    private TextView txtView_message_game;

    private String active_username, active_name_game, temp_key;
    private String chat_message_game, chat_username_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_team_building);

        active_username = getIntent().getExtras().get("username").toString();
        active_name_game = getIntent().getExtras().get("game_names").toString();

        cursor = FirebaseDatabase.getInstance().getReference().child("Games_list").child(active_name_game).child("chat");
        cursor.addChildEventListener(cursor_childListener);

        // set titre de la view
        setTitle("Partie: "+ active_name_game);

        btn_send_game = (Button)findViewById(R.id.btn_send_game);
        txtField_message_game = (EditText)findViewById(R.id.txtField_message_game);
        txtView_message_game = (TextView) findViewById(R.id.txtView_chat_game);

        btn_send_game.setOnClickListener(btn_sendListener);

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
}
