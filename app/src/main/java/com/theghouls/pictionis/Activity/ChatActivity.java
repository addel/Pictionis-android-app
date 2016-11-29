package com.theghouls.pictionis.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theghouls.pictionis.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Button btn_send;
    private EditText txtField_message;
    private TextView txtView_message;

    private String active_username, active_name_channel, temp_key;
    private String chat_message, chat_username;

    // Firebase database ref
    private DatabaseReference cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btn_send = (Button)findViewById(R.id.btn_send);
        txtField_message = (EditText)findViewById(R.id.txtField_message);
        txtView_message = (TextView) findViewById(R.id.txtView_chat);

        active_username = getIntent().getExtras().get("username").toString();
        active_name_channel = getIntent().getExtras().get("channel_name").toString();

        cursor = FirebaseDatabase.getInstance().getReference().child(active_name_channel);


        // set titre de la view
        setTitle("channel: "+ active_name_channel);

        btn_send.setOnClickListener(btn_sendListener);
        cursor.addChildEventListener(cursor_childListener);
    }

    private View.OnClickListener btn_sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // a chaque fois qu'on insert dans la db elle genere un UUID (comme la methode mysql_inserted_id() )
            // on la recupere, ce sera l'ID du message
            // on aurai pu créer un random id sans passer par ce trick mais en java c'est au moins 5 import de class mystic et 10 ligne de code donc sql like
            temp_key = cursor.push().getKey();

            // on creer une deuxieme reference (ce qu'on vient tout juste de creer )
            DatabaseReference message_cursor = cursor.child(temp_key);
            Map<String, Object> mapValue  = new HashMap<String, Object>();

            // populate
            mapValue.put("name", active_username);
            mapValue.put("message", txtField_message.getText().toString());

            message_cursor.updateChildren(mapValue);

        }
    };

    private ChildEventListener cursor_childListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Iterator i =dataSnapshot.getChildren().iterator();

            while(i.hasNext()){
                chat_message = (String)(((DataSnapshot) i.next()).getValue());
                chat_username = (String)(((DataSnapshot) i.next()).getValue());

                txtView_message.append(chat_username + ":" + chat_message + "\n");
                createNotification(chat_message);
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


    /////////////////
    /// HELPER /////
    ////////////////

    private void createNotification(String message) {

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Channel: "+chat_username+" => Message de" + active_username)
                .setContentText(message).setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}
