package com.theghouls.pictionis.Model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by admin on 23/11/2016.
 */

public class Drawing {

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Paint drawPaint, canvasPaint;
    private Path drawPath;
    private int paintColor = 0xFF660000;

    public Drawing() {
    }

    public Drawing(Canvas drawCanvas, Bitmap canvasBitmap, Paint drawPaint, Paint canvasPaint, Path drawPath, int paintColor) {
        this.drawCanvas = drawCanvas;
        this.canvasBitmap = canvasBitmap;
        this.drawPaint = drawPaint;
        this.canvasPaint = canvasPaint;
        this.drawPath = drawPath;
        this.paintColor = paintColor;
    }

    public Canvas getDrawCanvas() {
        return drawCanvas;
    }

    public void setDrawCanvas(Canvas drawCanvas) {
        this.drawCanvas = drawCanvas;
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
        this.canvasBitmap = canvasBitmap;
    }

    public Paint getDrawPaint() {
        return drawPaint;
    }

    public void setDrawPaint(Paint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public Paint getCanvasPaint() {
        return canvasPaint;
    }

    public void setCanvasPaint(Paint canvasPaint) {
        this.canvasPaint = canvasPaint;
    }

    public Path getDrawPath() {
        return drawPath;
    }

    public void setDrawPath(Path drawPath) {
        this.drawPath = drawPath;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }
}
