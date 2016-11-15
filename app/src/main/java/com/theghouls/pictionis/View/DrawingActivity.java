package com.theghouls.pictionis.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.theghouls.pictionis.R;

public class DrawingActivity extends AppCompatActivity {

    //////////////////
    /// DECLARATION///
    //////////////////

    // UI pointer
    private DrawingView drawView;

    private float small_tick, medium_tick, large_tick;

    // button pour la couleur du color_button
    private ImageButton color, erasebtn, btn_draw, new_draw_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.setThickSize(medium_tick);

        erasebtn = (ImageButton)findViewById(R.id.btnErase);
        erasebtn.setOnClickListener(erasebtnListener);

        small_tick = getResources().getInteger(R.integer.small_size);
        medium_tick = getResources().getInteger(R.integer.medium_size);
        large_tick = getResources().getInteger(R.integer.large_size);

        btn_draw = (ImageButton)findViewById(R.id.btnThick);
        btn_draw.setOnClickListener(btn_drawListener);

        new_draw_btn = (ImageButton)findViewById(R.id.btnNew_paint);
        new_draw_btn.setOnClickListener(new_draw_btnListener);


        // ici on a la barre des button de couleur
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);

        // ici au premier bouton je lui met le style definie dans le .xml
        color = (ImageButton)paintLayout.getChildAt(0);
        color.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_button_pressed));



    }

    ////////////////
    ////LISTENER///
    //////////////

    private View.OnClickListener new_draw_btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(DrawingActivity.this);
            newDialog.setTitle("Nouveau dessin");
            newDialog.setMessage("Attenttion: un nouveau dessin ecrase l'ancien, étes vous sur?");
            newDialog.setPositiveButton("casse pas les couilles", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.new_paint();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("je suis une merde missClick", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });

            newDialog.show();
        }
    };

    private View.OnClickListener erasebtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog thick_dialog = new Dialog(DrawingActivity.this);
            thick_dialog.setTitle("Epaisseur du de la gomme");

            thick_dialog.setContentView(R.layout.thick_selector);


            View.OnClickListener smallbtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setThickSize(small_tick);
                    thick_dialog.dismiss();
                }
            };
            final View.OnClickListener mediumbtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setThickSize(medium_tick);
                    thick_dialog.dismiss();
                }
            };
            final View.OnClickListener largebtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setThickSize(large_tick);
                    thick_dialog.dismiss();
                }
            };

            ImageButton small = (ImageButton)thick_dialog.findViewById(R.id.small_thick);
            small.setOnClickListener(smallbtnListener);

            ImageButton medium = (ImageButton)thick_dialog.findViewById(R.id.medium_thick);
            medium.setOnClickListener(mediumbtnListener);

            ImageButton large = (ImageButton)thick_dialog.findViewById(R.id.large_thick);
            large.setOnClickListener(largebtnListener);

            thick_dialog.show();
        }
    };

    private View.OnClickListener btn_drawListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog thick_dialog = new Dialog(DrawingActivity.this);
            thick_dialog.setTitle("Epaisseur du trait");

            thick_dialog.setContentView(R.layout.thick_selector);


            View.OnClickListener smallbtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setThickSize(small_tick);
                    drawView.setLast_thick_size(small_tick);
                    drawView.setErase(false);
                    thick_dialog.dismiss();
                }
            };
            final View.OnClickListener mediumbtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setThickSize(medium_tick);
                    drawView.setLast_thick_size(medium_tick);
                    drawView.setErase(false);
                    thick_dialog.dismiss();
                }
            };
            View.OnClickListener largebtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setThickSize(large_tick);
                    drawView.setLast_thick_size(large_tick);
                    drawView.setErase(false);
                    thick_dialog.dismiss();
                }
            };

            ImageButton small = (ImageButton)thick_dialog.findViewById(R.id.small_thick);
            small.setOnClickListener(smallbtnListener);

            ImageButton medium = (ImageButton)thick_dialog.findViewById(R.id.medium_thick);
            medium.setOnClickListener(mediumbtnListener);

            ImageButton large = (ImageButton)thick_dialog.findViewById(R.id.large_thick);
            large.setOnClickListener(largebtnListener);

            thick_dialog.show();
        }
    };


    public void btnColorClicked(View view){

        drawView.setErase(false);
        drawView.setThickSize(drawView.getLast_thick_size());
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
