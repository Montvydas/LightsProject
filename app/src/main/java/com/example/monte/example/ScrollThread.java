package com.example.monte.example;

import android.content.Context;
import android.view.View;

/**
 * Created by monte on 26/08/2015.
 */
public class ScrollThread implements Runnable{
    private boolean toRun = false;
    private float angleSpeed = 0;
    private float angle = 0;
    Thread thread = null;

    public void pause(){
        toRun = false;
        while (true){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume(){
        toRun = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        if (toRun == true) {
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
