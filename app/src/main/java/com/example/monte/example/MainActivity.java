package com.example.monte.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    LinearLayout myLayout;
    MainView mainView;
    private float X, Y, prevX, prevY;
    private StoredCoordinates myCoordinates = StoredCoordinates.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = new MainView(this);
        setContentView(R.layout.activity_main);
        myLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
        myLayout.addView(mainView);

        //myCoordinates = StoredCoordinates.INSTANCE;
        startUpdateViewThread();
        startMotionEventThread();
        changeColorWithDelay();

    }

    private void changeColorWithDelay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //get Previous Color

                //get Current Color

                //get 4 of a difference between colors

                //apply
            }
        }).start();
    }

    private void setMousePosition(float X, float Y, float prevX, float prevY, boolean isPressed) {
        myCoordinates.X = X;
        myCoordinates.Y = Y;
        myCoordinates.prevX = prevX;
        myCoordinates.prevY = prevY;
        myCoordinates.isPressed = isPressed;
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
                    mainView.postInvalidate();
                    //Log.e("Thread", "works");
                }
            }
        }).start();
    }

    private void startMotionEventThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
/*
                    if (myCoordinates.isPressed == false) {
                        myCoordinates.angleSpeed *= 0.95;
                        if (myCoordinates.angleSpeed < 0.2 && myCoordinates.angleSpeed > -0.2)
                            myCoordinates.angleSpeed = 0;
                    } else {
                        myCoordinates.angleSpeed = (float) ((myCoordinates.prevY - myCoordinates.Y) * 0.5);
                        if (myCoordinates.angleSpeed < 1.0 && myCoordinates.angleSpeed > -1.0)
                            myCoordinates.angleSpeed = (float) Math.pow(myCoordinates.angleSpeed, 5.0);
                    }
                    */
                    float density = getResources().getDisplayMetrics().density;
                    mainView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:   //prev value now becomes equal to current value
                                    X = event.getX();
                                    Y = event.getY();
                                   // if (X < myCoordinates.circleSize) {
                                        setMousePosition(X, Y, X, Y, true);

                                    //}
                                    break;
                                case MotionEvent.ACTION_MOVE:   //prev value is different than current value
                                    prevY = Y;
                                    prevX = X;
                                    X = event.getX();
                                    Y = event.getY();

                                    //if (X < myCoordinates.circleSize) {

                                        setMousePosition(X, Y, prevX, prevY, true);
                                    //}
                                    break;
                                case MotionEvent.ACTION_UP: //to make post-touch scroll effect
                                    myCoordinates.isPressed = false;
                                    break;
                            }

                            return true;
                        }
                    });
                }
            }
        }).start();
    }
}

