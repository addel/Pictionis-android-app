package com.theghouls.pictionis;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ////// QUESTION onClick DANS LE XML VS OnCliCKListener
    ////// CUSTOM VIEW VS FRAGMENT


    // Firebase Auth
    private FirebaseAuth auth;

    private Button btn_drawActivity, btn_chatActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button
        btn_drawActivity = (Button) findViewById(R.id.go_to_draw);
        btn_chatActivity = (Button) findViewById(R.id.go_to_chat);

        // Listener
        btn_drawActivity.setOnClickListener(btn_drawActivityListener);
        btn_chatActivity.setOnClickListener(btn_chatActivityListener);

        auth = FirebaseAuth.getInstance();

        /*if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }*/
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
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
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

    public void signOut() {
        auth.signOut();
    }
}
