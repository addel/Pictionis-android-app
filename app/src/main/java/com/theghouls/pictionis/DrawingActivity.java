package com.theghouls.pictionis;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class DrawingActivity extends AppCompatActivity {

    //////////////////
    /// DECLARATION///
    //////////////////

    // UI pointer
    private DrawingView drawView;

    // button pour la couleur du color_button
    private ImageButton color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawView = (DrawingView)findViewById(R.id.drawing);

        // ici on a la barre des button de couleur
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);

        // ici au premier bouton je lui met le style definie dans le .xml
        color = (ImageButton)paintLayout.getChildAt(0);
        color.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_button_pressed));

    }

    public void btnColorClicked(View view){

        if(view!=color){
            // on get la color depuis le bouton clicker (je rappel que en mobile le master object est View donc un ImageButton est aussi une View
            ImageButton imgView = (ImageButton)view;
            String newColor = view.getTag().toString();

            // on set a la custom view
            drawView.setColor(newColor);

            // on update la vue
            imgView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_button_pressed));
            color.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_button));
            color=(ImageButton)view;
        }

    }


}
