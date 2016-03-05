package com.example.monte.example;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by monte on 26/08/2015.
 */
public class TouchEvents extends View implements View.OnTouchListener {
    private float prevX = 0;
    private float prevY = 0;
    private float X = 0;
    private float Y = 0;

    public TouchEvents(Context context) {
        super(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:   //prev value now becomes equal to current value
                X = event.getX();
                Y = event.getY();
                prevY = Y;
                prevX = X;
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:   //prev value is different than current value
                prevY = Y;
                prevX = X;
                X = event.getX();
                Y = event.getY();
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP: //to make post-touch scroll effect

                break;
        }
        return true;
    }
}
