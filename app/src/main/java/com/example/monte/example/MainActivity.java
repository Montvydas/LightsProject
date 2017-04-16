package com.example.monte.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import static android.graphics.Color.*;

public class MainActivity extends Activity implements CircleView.CircleViewListener{
    CircleView circleView;
    ImageView sendColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleView = (CircleView) findViewById(R.id.mainView);
        circleView.setupCircle(CircleView.rainbowColors);
        circleView.setHorizontalOrientation(0.0f);
        circleView.setCircleViewListener(this);

        sendColor = (ImageView) findViewById(R.id.send_color);
        startUpdateViewThread();
    }

    private void startUpdateViewThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(17);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    circleView.postInvalidate();
                    //Log.e("Thread", "works");
                }
            }
        }).start();
    }

    @Override
    public void onColorChange(int color) {
        Log.e("color", red(color) + " " + green(color) + " " + blue(color));
        sendColor.setBackgroundColor(color);
    }
}

