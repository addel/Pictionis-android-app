package com.theghouls.pictionis.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class channelChatActivity extends AppCompatActivity {

    // UI
    private Button add_channel_btn;
    private EditText name_channel_txtField;
    private ListView listView;

    // pour la listView
    private ArrayAdapter <String> arrayAdapter;
    private ArrayList <String> list_channel = new ArrayList<>();

    // variable
    private String username;

    // Firebase database ref
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference root = database.getReference().child("Channel_chat");

    // Firebase Auth
    private FirebaseAuth auth;
    // Firebase User
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_chat);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            username = user.getDisplayName();
        }

        // comme d'ab on recup les element de la vieux depuis le XMl
        add_channel_btn = (Button)findViewById(R.id.btn_add_channel);
        name_channel_txtField = (EditText)findViewById(R.id.txtField_add_channel);
        listView = (ListView)findViewById(R.id.listView_channel);

        // add listerner
        add_channel_btn.setOnClickListener(add_channel_btnListener);
        listView.setOnItemClickListener(listViewItemListener);

        // arrayAdpater necessaire pour les list View, en gros c'est lui qui fait la liason entre le datasource(list_channel- et la listView)
        // C'est grace a ce ArrayAdapter que la list view va etre nourri, il va transformer la list de string en list d'objet View pour la list View
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_channel);
        listView.setAdapter(arrayAdapter);


        root.addValueEventListener(rootValuesListener);
    }

    ////////////////
    ////LISTENER///
    //////////////

    private View.OnClickListener add_channel_btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // hash map vraiment opti ??????

            Map <String, Object> map = new HashMap<String, Object>();
            map.put(name_channel_txtField.getText().toString(), "");
            root.updateChildren(map);

            name_channel_txtField.getText().clear();
        }
    };

    private ValueEventListener rootValuesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // donc pour rappeler on est dans un une methode du listener qui porte bien son nom

            // on va utiliser un hashSet qui fait parti de la gamme d'objet pour les collection
            // on utilise le hashSet car il n'accepte pas les valeeur dupliqué
            Set<String> set = new HashSet<String>();
            // on va itérer sur la base de donnée
            // Iterator VS for loop ????
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

    private AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(channelChatActivity.this, ChatActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("channel_name", ((TextView) view).getText().toString());
            startActivity(intent);
        }
    };


}
