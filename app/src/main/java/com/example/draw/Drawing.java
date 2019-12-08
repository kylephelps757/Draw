package com.example.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class Drawing extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private ScaleGestureDetector mScaleGestureDetector;






    public Drawing(Context context) {
        this(context, null);
    }

    public Drawing(Context context, AttributeSet attrs) {

        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);


        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {


        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);


        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float mScaleFactor = MainActivity.getScaleFactor();
        boolean togglePen = MainActivity.getToggle();
        canvas.save();
        mCanvas.drawColor(backgroundColor);
            for (FingerPath fp : paths) {
                mPaint.setColor(fp.color);
                mPaint.setStrokeWidth(fp.strokeWidth);
                mPaint.setMaskFilter(null);

                if (fp.emboss)
                    mPaint.setMaskFilter(mEmboss);
                else if (fp.blur)
                    mPaint.setMaskFilter(mBlur);

                mCanvas.drawPath(fp.path, mPaint);
            }
//if (!togglePen) {
//        Log.d("TRANSLATE", "TRANSLATE");
//        mCanvas.translate(translateX / mScaleFactor, translateY / mScaleFactor);
        float translateX = MainActivity.getTranslateX();
        float translateY = MainActivity.getTranslateY();
//}
       // if (translateX > MainActivity.getOffsetX()) {
       //     translateX = MainActivity.getOffsetX();
       // }
       // if (translateY > MainActivity.getOffsetY()) {
       //     translateY = MainActivity.getOffsetY();
       // }
       // if (translateX > MainActivity.getOffsetX() + (1080f / mScaleFactor)) {
       //    translateX = MainActivity.getOffsetX();
       // }
       // if (translateY > MainActivity.getOffsetY() + (2220f / mScaleFactor)) {
       //     translateY = MainActivity.getOffsetY();
       // }

        Log.d("TESTXYXXXX",Float.toString(translateX));
        Log.d("TESTXYYYY",Float.toString(translateY));
        canvas.drawBitmap(mBitmap, translateX, translateY, mBitmapPaint);
        canvas.restore();
    }

    public void touchStart(float x, float y) {
        mPath = new Path();
        strokeWidth = MainActivity.getProgress();
        FingerPath fp = new FingerPath(MainActivity.currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }


    public void rescaleCanvas(float mScaleFactor) {
        mCanvas.save();
        mCanvas.scale(mScaleFactor,mScaleFactor);
        //mCanvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        //mCanvas.restore();
        Log.d("SCALED","SCAAAALE");
        String test1 = Float.toString(mScaleFactor);
        Log.d("Factor", test1);
        }




    public void touchUp() {
        mPath.lineTo(mX, mY);
        Log.d("Touchup", "touchup");
    }



}
