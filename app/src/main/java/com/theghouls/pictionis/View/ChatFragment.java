package com.theghouls.pictionis.View;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Activity.ChatActivity;
import com.theghouls.pictionis.Activity.GameActivity;
import com.theghouls.pictionis.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ChatFragment extends Fragment {

    private Button btn_send;
    private EditText txtField_message;
    private TextView txtView_message;

    private String active_username, active_name_channel, temp_key;
    private String chat_message, chat_username;

    private DatabaseReference refGameChat;

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        refGameChat= FirebaseDatabase.getInstance().getReference();
        refGameChat = ((GameActivity) getActivity()).getReferenceGame().child("chat");
        refGameChat.addChildEventListener(refGameListener);

        btn_send = (Button)view.findViewById(R.id.btn_send);
        txtField_message = (EditText)view.findViewById(R.id.txtField_message);
        txtView_message = (TextView) view.findViewById(R.id.txtView_chat);

        active_username = getArguments().getString("username");
        btn_send.setOnClickListener(btn_sendListener);

        return view;


    }

    //////////////////////
    //// LISTENER ///////
    ////////////////////

    private ChildEventListener refGameListener = new ChildEventListener() {
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

    private View.OnClickListener btn_sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            temp_key = refGameChat.push().getKey();

            DatabaseReference message_cursor = refGameChat.child(temp_key);
            Map<String, Object> mapValue  = new HashMap<String, Object>();

            mapValue.put("name", active_username);
            mapValue.put("message", txtField_message.getText().toString());

            message_cursor.updateChildren(mapValue);

            txtField_message.setText("");

        }
    };

    /////////////////
    /// HELPER /////
    ////////////////

    private void createNotification(String message) {

        Intent intent = new Intent(getActivity(), GameActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, 0);

        Notification noti = new Notification.Builder(getContext())
                .setContentTitle("Channel: "+chat_username+" => Message de" + active_username)
                .setContentText(message).setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}
