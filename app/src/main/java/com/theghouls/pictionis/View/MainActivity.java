package com.theghouls.pictionis.View;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.theghouls.pictionis.R;

import static com.theghouls.pictionis.R.id.go_to_pictionis;

public class MainActivity extends AppCompatActivity {

    ////// QUESTION onClick DANS LE XML VS OnCliCKListener
    ////// CUSTOM VIEW VS FRAGMENT
    ////// POURQUOI Finish();


    // Firebase Auth
    private FirebaseAuth auth;
    // Firebase User
    private FirebaseUser user;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button
        Button btn_drawActivity = (Button) findViewById(R.id.go_to_draw);
        Button btn_chatActivity = (Button) findViewById(R.id.go_to_chat);
        Button btn_pictionis = (Button) findViewById(R.id.go_to_pictionis);


        // Listener
        btn_drawActivity.setOnClickListener(btn_drawActivityListener);
        btn_chatActivity.setOnClickListener(btn_chatActivityListener);
        btn_pictionis.setOnClickListener(btn_pictionisListener);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        user = auth.getCurrentUser();
        if(user != null){
            username = user.getDisplayName();
        }


        if(username == null)
            user_name_alert();
    }

    ////////////////
    ////LISTENER///
    //////////////

    private View.OnClickListener btn_drawActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, DrawingActivity.class));
        }
    };

    private View.OnClickListener btn_chatActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, channelChatActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    };

    private View.OnClickListener btn_pictionisListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, GamesList.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    };

    /////////////////
    //// ToolBar ///
    ///////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // charge le menu créer
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.loggout:
                signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /////////////////
    //// Helper ////
    ///////////////

    private void signOut() {
        auth.signOut();
    }

    private void user_name_alert(){

        final EditText edittext = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        edittext.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Veullez entrez un nom d'utilisateur qui sera votre avatar dans cette application")
                .setTitle("Votre nom d'utilisateur");
        builder.setIcon(R.drawable.ic_pictionislogo);

        builder.setView(edittext);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String name = edittext.getText().toString().trim();
                FirebaseUser user = auth.getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                if (user != null) {
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("debug", "user profile update");
                                username = name;
                            }
                        }
                    });
                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                user_name_alert();
            }
        });

        builder.show();

    }
}
