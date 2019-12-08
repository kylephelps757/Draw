package com.example.draw;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ScaleGestureDetector;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.Button;
import android.widget.SeekBar;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.view.Gravity;
import android.graphics.Bitmap;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private Drawing paintView;
    private boolean lineWeightToggle;
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
    //private float x;
    //private float y;
    int[] zoomCoords = new int[2];
    private boolean panInterlock;
    Context context;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayoutLineWeight;


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
        ToggleButton penButton = (ToggleButton) findViewById(R.id.toggleButton);
        penButton.setChecked(true);
        penButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    penToggle = true;
                }
                    else{
                    penToggle = false;
                }
            }
        });








        final Button popUp = (Button) findViewById(R.id.popUp);
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("popup", "Popup");
                // Initialize a new instance of LayoutInflater service
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
                        Log.d("BUTTON", "BUtton");
                        // imageView.setImageBitmap(b);
                        // main.setBackground
                    }
                });
                ImageButton clear =customView.findViewById(R.id.ib_erase);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.clear();
                        // imageView.setImageBitmap(b);
                        // main.setBackgroundColor(Color.parseColor("#999999"));
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

                        Log.d("LINEWEIGHT", "LINEWEIGHT");

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
    public boolean onTouchEvent(MotionEvent event) {
        //float scale = event.getSize();
        int pointerCount = event.getPointerCount();
        x = (event.getX() / mScaleFactor) + offsetX ;
        y = (event.getY() / mScaleFactor) + offsetY;
        String test = Float.toString(x);
        Log.d("XXXX", test);
        String test1 = Float.toString(y);
        Log.d("YYYY", test1);

        if(pointerCount >= 2 && !penToggle) {
            mScaleGestureDetector.onTouchEvent(event);
            panInterlock = false;
        }
        else {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    panInterlock = true;
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
                        //if (translateX > offsetX) {
                        //    translateX = offsetX;
                       // }
                       // if (translateY > offsetY) {
                       //     translateY = offsetY;
                       // }
                        Log.d("TranX", Float.toString(paintView.getWidth()));
                        Log.d("Trany", Float.toString(paintView.getHeight()));
                        Log.d("OFFFSETX", Float.toString(translateX));
                        Log.d("OFFFSETy", Float.toString(translateY));
                        paintView.invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                   if (penToggle) {
                       paintView.touchUp();
                   }
                    paintView.invalidate();
                    //int left = rect.top;
                    paintView.getLocationOnScreen(zoomCoords);
                    String left = Integer.toString(zoomCoords[0]);
                    Log.d("Left", Float.toString(offsetX));
                    String top = Integer.toString(zoomCoords[1]);
                    Log.d("Top", Float.toString(offsetY));
                    String scaleF = Float.toString(mScaleFactor);
                    Log.d("scale", scaleF);
                    Log.d("toggle", Boolean.toString(penToggle));


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
            //float focusX = scaleGestureDetector.getFocusX();
            //float focusY = scaleGestureDetector.getFocusY();
            //x += (x - focusX) * (mScaleFactor);
            //y += (y - focusY) * (mScaleFactor);
            //Log.d("XFocus", Float.toString(focusX));
            paintView.setScaleX(mScaleFactor);
            paintView.setScaleY(mScaleFactor);
            //paintView.setX(500);
            //paintView.setY();
            return true;
        }
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    // MenuInflater menuInflater = getMenuInflater();
    //  menuInflater.inflate(R.menu.main, menu);
    // return super.onCreateOptionsMenu(menu);
    //}

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    switch(item.getItemId()) {
    //        case R.id.normal:
    //            paintView.normal();
    //            return true;
    //        case R.id.emboss:
    //            paintView.emboss();
    //            return true;
    //        case R.id.blur:
    //            paintView.blur();
    //            return true;
    //        case R.id.clear:
    //            paintView.clear();
    //            return true;
    //    }

    //    return super.onOptionsItemSelected(item);

  //  @Override
  //  public boolean onTouchEvent(MotionEvent event) {
  //      mScaleGestureDetector.onTouchEvent(event);
  //      return true;
  //  }

    public static float getOffsetX(){
        return (offsetX) ;
    }

    public static float getOffsetY(){
        return (offsetY);
    }

    public static float getabsoluteZeroY(){
        float absoluteZeroY = (translateY - offsetY) * -1;
        return absoluteZeroY;
    }

    public static float getAbsoluteZeroX(){
        float absoluteZeroX = (translateX - offsetX) * -1;
        return absoluteZeroX;
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
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }
}
