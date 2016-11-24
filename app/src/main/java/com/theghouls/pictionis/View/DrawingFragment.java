package com.theghouls.pictionis.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theghouls.pictionis.Activity.GameActivity;
import com.theghouls.pictionis.R;

public class DrawingFragment extends Fragment implements View.OnClickListener {

    //////////////////
    /// DECLARATION///
    //////////////////

    // UI pointer
    private DrawingView drawView;

    private float small_tick, medium_tick, large_tick;

    // button pour la couleur du color_button
    private ImageButton color, erasebtn, btn_draw, new_draw_btn;

    private DatabaseReference refGame;


    public DrawingFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        refGame = FirebaseDatabase.getInstance().getReference();
        refGame = ((GameActivity) getActivity()).getReferenceGame();
        refGame.addValueEventListener(refGameListener);

        drawView = (DrawingView)view.findViewById(R.id.drawing);
        drawView.setThickSize(medium_tick);
        drawView.setRefGame(refGame);
        drawView.setRealTime(true);


        erasebtn = (ImageButton)view.findViewById(R.id.btnErase);
        erasebtn.setOnClickListener(erasebtnListener);

        small_tick = getResources().getInteger(R.integer.small_size);
        medium_tick = getResources().getInteger(R.integer.medium_size);
        large_tick = getResources().getInteger(R.integer.large_size);

        btn_draw = (ImageButton)view.findViewById(R.id.btnThick);
        btn_draw.setOnClickListener(btn_drawListener);

        new_draw_btn = (ImageButton)view.findViewById(R.id.btnNew_paint);
        new_draw_btn.setOnClickListener(new_draw_btnListener);

        ImageButton btn_color1 = (ImageButton) view.findViewById(R.id.paint_colors1);
        btn_color1.setOnClickListener(this);
        ImageButton btn_color2 = (ImageButton) view.findViewById(R.id.paint_colors2);
        btn_color2.setOnClickListener(this);
        ImageButton btn_color3 = (ImageButton) view.findViewById(R.id.paint_colors3);
        btn_color3.setOnClickListener(this);
        ImageButton btn_color4 = (ImageButton) view.findViewById(R.id.paint_colors4);
        btn_color4.setOnClickListener(this);
        ImageButton btn_color5 = (ImageButton) view.findViewById(R.id.paint_colors5);
        btn_color5.setOnClickListener(this);
        ImageButton btn_color6 = (ImageButton) view.findViewById(R.id.paint_colors6);
        btn_color6.setOnClickListener(this);
        ImageButton btn_color7 = (ImageButton) view.findViewById(R.id.paint_colors7);
        btn_color7.setOnClickListener(this);


        Button btn_response = (Button) view.findViewById(R.id.btn_response);
        EditText txtField_response = (EditText)view.findViewById(R.id.txtField_response);



        // ici on a la barre des button de couleur
        LinearLayout paintLayout = (LinearLayout)view.findViewById(R.id.paint_colors);

        // ici au premier bouton je lui met le style definie dans le .xml
        color = (ImageButton)paintLayout.getChildAt(0);
        color.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.color_button_pressed));
        // Inflate the layout for this fragment


        return view;
    }

    ////////////////
    ////LISTENER///
    //////////////

    private ValueEventListener refGameListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String str = null;
            try {
                str = (String) dataSnapshot.child("drawing").getValue();
                drawView.retrieveBitmap(str);
            }catch (Exception e){
                Log.d("drawing", e.toString());
            }
            Log.i("drawing", str);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener new_draw_btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(getContext());
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
            final Dialog thick_dialog = new Dialog(getContext());
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
            final Dialog thick_dialog = new Dialog(getContext());
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.paint_colors1 || view.getId() == R.id.paint_colors2 || view.getId() == R.id.paint_colors3 || view.getId() == R.id.paint_colors4 || view.getId() == R.id.paint_colors5 || view.getId() == R.id.paint_colors6 || view.getId() == R.id.paint_colors7){

            drawView.setErase(false);
            drawView.setThickSize(drawView.getLast_thick_size());
            if(view!=color){

                // on get la color depuis le bouton clicker (je rappel que en mobile le master object est View donc un ImageButton est aussi une View
                ImageButton imgView = (ImageButton)view;
                String newColor = view.getTag().toString();

                // on set a la custom view
                drawView.setColor(newColor);

                // on update la vue
                imgView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.color_button_pressed));
                color.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.color_button));
                color=(ImageButton)view;
            }
        }
    }

    /////////////////
    ///// HELPER ////
    ////////////////


}
