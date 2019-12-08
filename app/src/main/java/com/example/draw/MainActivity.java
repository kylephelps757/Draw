package com.example.draw;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ScaleGestureDetector;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.SeekBar;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.view.Gravity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {
    private Drawing paintView;
    public ScaleGestureDetector mScaleGestureDetector;
    public static float mScaleFactor = 1.0f;
    private static float offsetX;
    private static float offsetY;
    public static boolean penToggle = true;
    float x;
    float y;
    float startX;
    float startY;
    public static int progress = 20;
    public static float translateX = 0;
    public static float translateY = 0;
    private SeekBar seekBar;
    private PopupWindow popupWindow;
    private PopupWindow lineweightWindow;
    int[] zoomCoords = new int[2];
    private boolean panInterlock;
    Context context;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayoutLineWeight;
    public static int currentColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        relativeLayout = (RelativeLayout) findViewById(R.id.popup);
        relativeLayoutLineWeight = (RelativeLayout) findViewById(R.id.lineweight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullScreen();
        paintView = (Drawing) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        paintView.init(metrics);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        final ToggleButton penButton = (ToggleButton) findViewById(R.id.toggleButton);
        penButton.setChecked(true);
        penButton.setText(null);
        penButton.setTextOn(null);
        penButton.setTextOff(null);
        Drawable pencil = AppCompatResources.getDrawable(context, R.drawable.icons8_pencil_drawing_50);
        pencil.setTint(ContextCompat.getColor(context,R.color.logoBlue));
        penButton.setBackgroundDrawable(pencil);
        penButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    penToggle = true;
                    Drawable pencil = AppCompatResources.getDrawable(context, R.drawable.icons8_pencil_drawing_50);
                    pencil.setTint(ContextCompat.getColor(context,R.color.logoBlue));
                    penButton.setBackgroundDrawable(pencil);
                }
                    else{
                    penToggle = false;
                    Drawable hand = AppCompatResources.getDrawable(context, R.drawable.icons8_hand_50_2);
                    hand.setTint(ContextCompat.getColor(context,R.color.logoBlue));
                    penButton.setBackgroundDrawable(hand);
                }
            }
        });

        final ImageButton popUp = (ImageButton) findViewById(R.id.popUp);
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFullScreen();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup,null);
                popupWindow = new PopupWindow(
                        customView,
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                );

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                ImageButton btn =customView.findViewById(R.id.ib_share);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Screenshot.takeScreenshotOfRootView(paintView, context);
                    }
                });

                ImageButton clear =customView.findViewById(R.id.ib_erase);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(true);
                        builder.setTitle("Erase All");
                        builder.setMessage("Are you sure you want to delete the whole thing?");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        paintView.clear();
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
                ImageButton lineWeight =customView.findViewById(R.id.ib_lineweight);
                lineWeight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        LayoutInflater inflaterLineWeight = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View lineweightView = inflaterLineWeight.inflate(R.layout.lineweight,null);
                        lineweightWindow = new PopupWindow(
                                lineweightView,
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                        );

                        ImageButton lineweightCheck = (ImageButton) lineweightView.findViewById(R.id.lineweightcheck);
                        lineweightCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                lineweightWindow.dismiss();
                            }
                        });

                        seekBar = (SeekBar) lineweightView.findViewById(R.id.seekBar);
                        seekBar.setProgress(progress);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                progress = seekBar.getProgress();
                                Log.d("Seekbar", String.valueOf(progress));
                            }
                        });
                        lineweightWindow.showAtLocation(paintView, Gravity.CENTER,0,0);
                    }
                });

                ImageButton color =customView.findViewById(R.id.ib_color);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ColorPicker colorPicker = new ColorPicker(MainActivity.this);
                        colorPicker.setColors(Color.parseColor("black"), Color.parseColor("white"), Color.parseColor("red"), Color.parseColor("blue"), Color.parseColor("green"),Color.parseColor("#f58231"), Color.parseColor("grey"), Color.parseColor("cyan"), Color.parseColor("magenta"), Color.parseColor("yellow"), Color.parseColor("lightgrey"), Color.parseColor("darkgrey"), Color.parseColor("maroon"), Color.parseColor("navy"), Color.parseColor("olive"), Color.parseColor("purple"), Color.parseColor("teal"));
                        colorPicker.show();
                        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position,int color) {
                                Log.d("POSITION", String.valueOf(position) );
                                Log.d("COLOR", String.valueOf(color) );
                                currentColor = color;
                                popupWindow.dismiss();
                            }
                            @Override
                            public void onCancel(){
                            }
                        });
                    }
                });
                popupWindow.showAtLocation(paintView, Gravity.CENTER,0,0);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setFullScreen();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setFullScreen();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        x = (event.getX() / mScaleFactor) + offsetX ;
        y = (event.getY() / mScaleFactor) + offsetY;

        if(pointerCount >= 2 && !penToggle) {
            mScaleGestureDetector.onTouchEvent(event);
            panInterlock = false;
        }
        else {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    panInterlock = true;
                    offsetX = (float) Math.abs(zoomCoords[0]) / mScaleFactor;
                    offsetY = (float) Math.abs(zoomCoords[1]) / mScaleFactor;
                    x = (event.getX() / mScaleFactor) + offsetX - translateX;
                    y = (event.getY() / mScaleFactor) + offsetY - translateY;
                    if (penToggle) {
                        paintView.touchStart(x, y);
                        paintView.invalidate();
                    }
                    else{
                        startX = event.getX() - translateX;
                        startY = event.getY() - translateY;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    offsetX = (float) Math.abs(zoomCoords[0]) / mScaleFactor;
                    offsetY = (float) Math.abs(zoomCoords[1]) / mScaleFactor;
                    x = (event.getX() / mScaleFactor) + offsetX - translateX;
                    y = (event.getY() / mScaleFactor) + offsetY - translateY;
                    if (penToggle) {
                        paintView.touchMove(x, y);
                        paintView.invalidate();
                    }
                    else if(panInterlock){
                        translateX = event.getX() - startX;
                        translateY = event.getY() - startY;
                        paintView.invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                   if (penToggle) {
                       paintView.touchUp();
                   }
                    paintView.invalidate();
                    paintView.getLocationOnScreen(zoomCoords);
                    break;
            }
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));
            paintView.setScaleX(mScaleFactor);
            paintView.setScaleY(mScaleFactor);
            return true;
        }
    }

    public static int getProgress(){
        return progress;
    }

    public static float getTranslateX(){
        return translateX;
    }

    public static float getTranslateY(){
        return translateY;
    }

    public static float getScaleFactor(){
        return mScaleFactor;
    }

    public static boolean getToggle(){
        return penToggle;
    }

    void setFullScreen(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                |View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
