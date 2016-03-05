package com.example.monte.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by monte on 24/08/2015.
 */
public class MyActivity extends Activity implements View.OnTouchListener{

    MyView myView;
    private float X = 0;
    private float Y = 0;
    private float prevX = 0;
    private float prevY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView(this);
        setContentView(myView);
        myView.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.pause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (event.getAction() & event.ACTION_MASK){
            case MotionEvent.ACTION_MOVE:
                prevY = Y;
                prevX = X;
                X = event.getX();
                Y = event.getY();

                myView.setX(X);
                myView.setY(Y);
                myView.setPrevX(prevX);
                myView.setPrevY(prevY);
                return true;
            case MotionEvent.ACTION_DOWN:
                myView.setIsPressed(true);
                X = event.getX();
                Y = event.getY();
                prevY = Y;
                prevX = X;
                myView.setPrevX(prevX);
                myView.setPrevY(prevY);
                myView.setX(X);
                myView.setY(Y);
                return true;
            case MotionEvent.ACTION_UP:
                myView.setIsPressed(false);
                return true;
        }
        /*
        if (((event.getAction() & event.ACTION_MASK) == MotionEvent.ACTION_MOVE) || ((event.getAction() & event.ACTION_MASK) == MotionEvent.ACTION_DOWN) ){
            X = event.getX();
            Y = event.getY();

            myView.setX((int) X);
            myView.setY((int) Y);

            return true;
        }
        */
        return false;
    }
}
