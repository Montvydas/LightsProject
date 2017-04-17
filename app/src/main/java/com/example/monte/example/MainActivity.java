package com.example.monte.example;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bluetooth.BlunoLibrary;

import static android.graphics.Color.*;

public class MainActivity extends BlunoLibrary implements CircleView.CircleViewListener, View.OnClickListener{
    CircleView circleView;
    ImageView sendColor;
    Button buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();
        serialBegin(115200);

        circleView = (CircleView) findViewById(R.id.mainView);
        circleView.setupCircle(CircleView.rainbowColors);
        circleView.setHorizontalOrientation(0.0f);
        circleView.setCircleViewListener(this);

        buttonScan = (Button) findViewById(R.id.connect_button); 
        buttonScan.setOnClickListener(this);
        sendColor = (ImageView) findViewById(R.id.send_color);
        startUpdateViewThread();
        setStatusBarTranslucent(true, getWindow());

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(3);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        String[] displayedValues = {"Constant", "Blink", "Rotate", "Multicolor"};
        np.setDisplayedValues(displayedValues);
        setNumberPickerTextColor(np, Color.BLACK);
//        ((Paint)selectorWheelPaintField.get(np)).setColor(color);

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                serialSend(String.valueOf("m"+newVal));
            }
        });
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
    protected void onResume() {
        super.onResume();
        onResumeProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    @Override
    public void onColorChange(int color) {
        Log.e("color", red(color) + " " + green(color) + " " + blue(color) + " color=" + color);
        sendColor.setBackgroundColor(color);

        if (isConnected){
//            byte[] value = my_int_to_bb_be(color);
//            serialSend(my_int_to_bb_be(color));
//            for (int i = 0; i < value.length; i++){
//                System.out.print(value[i] + " ");
//            }
//            System.out.println();
            serialSend(String.valueOf("c"+color));
        }
    }

    public static  byte[] my_int_to_bb_le(int myInteger){
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myInteger).array();
    }

    public static int my_bb_to_int_le(byte [] byteBarray){
        return ByteBuffer.wrap(byteBarray).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static  byte[] my_int_to_bb_be(int myInteger){
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(myInteger).array();
    }

    public static int my_bb_to_int_be(byte [] byteBarray){
        return ByteBuffer.wrap(byteBarray).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    boolean isConnected = false;
    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        isConnected = false;
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                isConnected = true;
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String data) {
//        Log.e("received", data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.connect_button:
                buttonScanOnClickProcess();
                break;
        }
    }

    // Method which makes the status bar translucent / transparent
    static void setStatusBarTranslucent(boolean makeTranslucent, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (makeTranslucent) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);

                    int MY_DIP_VALUE = 18;
                    int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            MY_DIP_VALUE, getResources().getDisplayMetrics());
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setTextSize(pixel);

                    ((Paint)selectorWheelPaintField.get(numberPicker)).setTypeface(Typeface.DEFAULT_BOLD);

                    ((EditText)child).setTextColor(color);
                    ((EditText)child).setTypeface(Typeface.DEFAULT_BOLD);
                    ((EditText)child).setTextSize(MY_DIP_VALUE);

                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setPickerTextColor", e);
                }
            }
        }
        return false;
    }
}

