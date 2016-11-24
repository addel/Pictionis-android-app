package com.theghouls.pictionis.View;

import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Activity.GameActivity;
import com.theghouls.pictionis.R;

public class ChatFragment extends Fragment {

    private Button btn_send;
    private EditText txtField_message;
    private TextView txtView_message;

    private String active_username, active_name_channel, temp_key;
    private String chat_message, chat_username;

    private DatabaseReference refGameChat;

    public ChatFragment() {

    }

    // un boutton qui clear tout le chat (seulement la textview hein




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        refGameChat= FirebaseDatabase.getInstance().getReference();
        refGameChat = ((GameActivity) getActivity()).getReferenceGame();
        refGameChat.addValueEventListener(refGameListener);

        return view;


    }

    //////////////////////
    //// LISTENER ///////
    ////////////////////

    private ValueEventListener refGameListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
