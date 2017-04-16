package com.example.monte.example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by monte on 24/08/2015.
 */
public class MyView extends SurfaceView implements Runnable{

    SurfaceHolder surfaceHolder;
    Thread thread = null;
    boolean toRun = false;
    private float X = 0;
    private float Y = 0;
    private float angle = 0;
    private float prevY = 0;
    private float prevX = 0;
    private boolean isPressed = false;
    private float angleSpeed = 0;

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public void setPrevX(float prevX) {
        this.prevX = prevX;
    }

    public void setPrevY(float prevY) {

        this.prevY = prevY;
    }

    public void setY(float y) {
        Y = y;
    }

    public void setX(float x) {
        X = x;
    }

    public MyView(Context context) {
        super(context);
        surfaceHolder = getHolder();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (toRun == true){
            if (!surfaceHolder.getSurface().isValid())
                continue;
            Canvas canvas = surfaceHolder.lockCanvas();
            draw(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void onDraw (Canvas canvas){

        if (isPressed == false){
            angleSpeed *= 0.91;
        } else {
            angleSpeed = (float) ((prevY - Y)*0.5);
        }
        angle -=  angleSpeed;     //apply a specific multiplier to make the rotation speed to correspond to the scrolling speed

        float density = getResources().getDisplayMetrics().density;
        Paint paint = new Paint();      //new paint to work with
        paint.setStyle(Paint.Style.FILL);//fill with no borders
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //anti aliasing filter

        paint.setColor(getPixelColor((int) angle)); //get the colour depending on the rotation angle
        canvas.drawPaint(paint);    //draw background

        int[] rainbow = getRainbowColors(); //get the colours to fill in the circle
        Shader shader = new SweepGradient(0,getHeight()/2,rainbow, null);   //add a shader with the rainbow colors

        //make the colour circle to rotate
        Matrix matrix = new Matrix(); //new matrix
        matrix.setRotate((int) angle, 0, getHeight()/2);    //rotate
        shader.setLocalMatrix(matrix);  //apply matrix on the shader

        paint.setShader(shader);    //apply shader on the paint
        canvas.drawCircle(0, getHeight() / 2, 200 * density, paint);  //show the shader at specific location only

        Paint cPaint = new Paint ();
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(Color.BLACK);
        canvas.drawCircle(X, Y, 25*density, cPaint);
        //Log.e("what the hell", X + " " + Y);
    }

    public void pause() {
        toRun = false;
        while (true){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        toRun = true;
        thread = new Thread(this);
        thread.start();
    }

    private int[] getRainbowColors() {
        return new int[] {
                Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA,
                Color.RED
        };
    }
    public int getPixelColor (int angle){
        angle = -angle; //revert angle to increment it by going anti-clockwise
        int[] rainbow = getRainbowColors(); //get the the colors
        int colorCount = rainbow.length - 1;    //get the number of colors excluding the last color which is the same as the first...
        //Log.e("Color Count is", colorCount + "");

        float stepDiff = (float) (360.0/colorCount);    //get the angle between two complete colours
        //Log.e("Step Difference is", stepDiff + "");

        int[] r = new int [colorCount]; //create arrays to store single values of red/green/blue colors
        int[] g = new int [colorCount]; //for one rainbow color
        int[] b = new int [colorCount];

        for (int i = 0; i < colorCount; i++){   //extract each color of the rainbow color
            //Log.e("red is", Color.red((rainbow[i])) + "");
            r[i] = Color.red(rainbow[i]);
            g[i] = Color.green(rainbow[i]);
            b[i] = Color.blue((rainbow[i]));
        }

        while (angle < 0){    //use only positive numbers for the angle
            angle += 360;
        }
        while (angle > 360){    //use only numbers less than 360
            angle -= 360;
        }

        //Log.e("Angle is", angle + "");

        float[] redStep = new float [colorCount];   //step value per degree for each color
        float[] greenStep = new float [colorCount];
        float[] blueStep = new float[colorCount];

        for (int i = 0; i < colorCount; i++){
            if (i == colorCount - 1){
                //float redDiff =  ((r[0] - r[i])/stepDiff);
                //Log.e("Red diff is", redDiff + "");
                redStep[i] = (int) ((r[0] - r[i])/stepDiff);
                greenStep[i] = (int) ((g[0] - g[i])/stepDiff);
                blueStep[i] = (int) ((b[0] - b[i])/stepDiff);
            } else {
                //float redDiff =  ((r[i+1] - r[i])/stepDiff);
                //Log.e("Red diff is", redDiff + "");
                redStep[i] = (int) ((r[i+1] - r[i])/stepDiff); //next color minus current color divided by the number of degrees between them
                greenStep[i] = (int) ((g[i+1] - g[i])/stepDiff);//later multiply by the angle to find the difference between real colour
                blueStep[i] = (int) ((b[i+1] - b[i])/stepDiff);//and the gradient colour
            }
        }

        int inBetween = 0;
        while (angle > stepDiff){ //find between which colours the angle is
            angle = (int) (angle - stepDiff);   //this also makes the angle to be between 0 and the stepDiff
            inBetween++;
        }
        if (inBetween > colorCount)     //sometimes a few degrees diff appears because float is not perfect...
            inBetween = colorCount;

        //Log.e("in Between these", inBetween + "");
        //Log.e("Angle is", angle+"");
        //int red = r[inBetween];
        //Log.e("red color factor is", "" + red);

        int color = Color.rgb(r[inBetween] + (int)(angle * redStep[inBetween]),      //find the color depending on the stepSize and the angle
                g[inBetween] + (int)(angle * greenStep[inBetween]),           //original color + angle * incrementing step
                b[inBetween] + (int)(angle * blueStep[inBetween]));        //that's why angle had to be reverted as it was incrementing to the other side

        return color;
    }

}
