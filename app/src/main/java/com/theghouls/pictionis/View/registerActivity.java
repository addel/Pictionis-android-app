package com.theghouls.pictionis.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.theghouls.pictionis.R;

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

        // Cette methode va permettre de désérialiser le XML de l'activité ce trouvant dans res => layout => nomDuXML.xml
        // La désértialisation ce fait dans le onCreate et la manipulation d'objet graphique toujours apres la methode ci dessous
        // Les differentes instances d'objet graphique (boutton, texte, image ect... sont gérer par cette methode
        setContentView(R.layout.activity_register);

        // on créer une instance de Firebase Auth, vous devriez devinnette quel pattern est utiliser
        auth = FirebaseAuth.getInstance();

        // On va retrouver les pointeur vers les objet graphique gerer par la methode setContentView
        // Biensur on fait ça apres la methode setContentView et pas avant sinon le compil va vous cracher à la gueule
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

            String email = txtFieldMail.getText().toString();
            String pwd = txtFieldPassword.getText().toString();

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
                                finish();
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
            finish();
        }
    };

    private View.OnClickListener btnResetPwd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final EditText edittext = new EditText(registerActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            edittext.setLayoutParams(lp);

            AlertDialog.Builder builder = new AlertDialog.Builder(registerActivity.this);

            builder.setMessage(R.string.dialog_reset_message)
                    .setTitle(R.string.dialog_reset_title);
            builder.setIcon(R.drawable.ic_pictionislogo);

            builder.setView(edittext);

            builder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    String email = edittext.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplication(), R.string.infoMail, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(registerActivity.this, R.string.failToSendResetMail, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

            builder.setNegativeButton("Annulez", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    };

    /////////////
    ///HELPER///
    ///////////

    public final boolean isInternetConnection(){
        getApplicationContext();
        ConnectivityManager con = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
