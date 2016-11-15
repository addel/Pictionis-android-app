package com.theghouls.pictionis.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.theghouls.pictionis.R;

public class DrawingView extends View {

    //////////////////////////////
    /// DECLARATION VARIABLE ////
    ////////////////////////////

    // la surface sur lequel on va dessiné et qui va s'occuper du transfer a la vue
    private Canvas drawCanvas;

    // va ajouter les pixel qui vont bien (arg de drawCanvas)
    private Bitmap canvasBitmap;

    // drawPaint => propieter du dessin (couleur, size ect) / canvasPaint => ce qu'on va dessiné (arg de drawCanvas)
    private Paint drawPaint, canvasPaint;

    // le tracé du dessin
    private Path drawPath;

    // coleur de base
    private int paintColor = 0xFF660000;

    private boolean erase = false;

    private float thick_size, last_thick_size;



    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        thick_size = getResources().getInteger(R.integer.medium_size);
        last_thick_size = thick_size;

        // ici on va pouvoir (plus tard ) modifier les propieter du dessin via saisie utilisateur
        // pour l'instant en HARD coding
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(thick_size);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    public void setColor(String newColor){
        invalidate();

        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setErase(boolean isErase){
        erase = isErase;

        // setXfermode delete une section de bitmap
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    public void setThickSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        thick_size=pixelAmount;
        drawPaint.setStrokeWidth(thick_size);
    }

    public void setLast_thick_size(float lastSize){
        last_thick_size=lastSize;
    }

    public float getLast_thick_size(){
        return last_thick_size;
    }

    public void new_paint(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }


    ///////////////////////////
    //// CYCLE LIFEZ METHODE ///
    ///////////////////////////

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // a vous de vous learn sur les bitmap
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;

        }

        invalidate();
        return true;

    }
}
