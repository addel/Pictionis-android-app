package com.theghouls.pictionis;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class registerActivity extends AppCompatActivity {

    // UI Pointer
    private ProgressBar progressBarView;
    private EditText txtFieldMail, txtFieldPassword;
    private Button registerButton, loginButton, resetPasswordButton;

    // Firebase Auth
    private FirebaseAuth auth;

    /////////////////////
    //// CYCLE LIFE ////
    ///////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        auth = FirebaseAuth.getInstance();


        progressBarView = (ProgressBar) findViewById(R.id.registerViewProgressBar);
        txtFieldMail = (EditText) findViewById(R.id.txtFieldMail);
        txtFieldPassword = (EditText) findViewById(R.id.txtFieldPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.gotoLogin);
        resetPasswordButton = (Button) findViewById(R.id.forgetAccountButton);

        // Listener
        registerButton.setOnClickListener(btnRegisterListener);
        loginButton.setOnClickListener(btnLoginListener);
        resetPasswordButton.setOnClickListener(btnResetPwd);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBarView.setVisibility(View.GONE);
    }

    ////////////////
    ////LISTENER///
    //////////////

    private View.OnClickListener btnRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("TestOnRegisterButon", "OK on register click ");

            String email = txtFieldMail.getText().toString();
            String pwd = txtFieldPassword.getText().toString();

            Log.i("TestOnloginButton", email);
            Log.i("TestOnloginButton", pwd);

            if (isInternetConnection()){
                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.infoMail, Toast.LENGTH_LONG).show();
                    return;
                }

                if(pwd.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.infosPwdEmpty, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!pwd.isEmpty() && (pwd.length() < 6)){
                    Toast.makeText(getApplicationContext(), R.string.infosPwdTooSmall, Toast.LENGTH_LONG).show();
                    return;
                }
            }else{
                return;
            }

            progressBarView.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBarView.setVisibility(View.GONE);

                            if(task.isSuccessful()){
                                startActivity(new Intent(registerActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(registerActivity.this, "Authent failed"+ task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(registerActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(registerActivity.this, "createUserWithEmailAndPassword"+ e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };

    private View.OnClickListener btnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("TestOnloginButton", "OK on login click ");

        }
    };

    private View.OnClickListener btnResetPwd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("TestresetPasswordButton", "OK on reset click ");
        }
    };

    /////////////
    ///HELPER///
    ///////////

    public final boolean isInternetConnection(){
        ConnectivityManager con = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = con.getActiveNetworkInfo();

        if(activeNetwork != null){
            Toast.makeText(getApplicationContext(), getString(R.string.connectionOK) + activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(getApplicationContext(), R.string.connectionInternetNotOK, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
