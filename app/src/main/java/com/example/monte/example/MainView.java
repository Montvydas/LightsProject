package com.example.monte.example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Color.*;

/**
 * Created by monte on 26/08/2015.
 */
public class MainView extends View {
    private StoredCoordinates myCoordinates = StoredCoordinates.INSTANCE;

    //public abstract void lol ();

    private int[] getRainbowColors() {  //define rainbow colors
        return new int[]{
                RED,
                YELLOW,
                GREEN,
                CYAN,
                BLUE,
                MAGENTA,
                RED   //Need the first color to be the same as the last color for gradient to be even at all places
        };
    }

    public MainView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        //need to add all this code to onTouch thread
       /*
        if (myCoordinates.isPressed == false) {
            myCoordinates.angleSpeed *= 0.95;
            if (myCoordinates.angleSpeed < 0.2 && myCoordinates.angleSpeed > -0.2)
                myCoordinates.angleSpeed = 0;
        } else {
            myCoordinates.angleSpeed = (float) ((myCoordinates.prevY - myCoordinates.Y) * 0.4);
            if (myCoordinates.angleSpeed < 1.0 && myCoordinates.angleSpeed > -1.0)
                myCoordinates.angleSpeed = (float) Math.pow(myCoordinates.angleSpeed, 5.0);
        }
        */
        //get the pixel density
        float density = getResources().getDisplayMetrics().density;
        //Log.e("AngleSpeed is", angleSpeed + "");

        myCoordinates.angleSpeed = (float) ((myCoordinates.prevY - myCoordinates.Y) * 0.4);
        myCoordinates.angle -= myCoordinates.angleSpeed;     //apply a specific multiplier to make the rotation speed to correspond to the scrolling speed

        Paint paint = new Paint();      //new paint to work with
        paint.setStyle(Paint.Style.FILL);//fill with no borders
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //anti aliasing filter

        int[] rainbow = getRainbowColors(); //get the colours to fill in the circle
        //paint.setColor(getPixelColor((int) angle, rainbow)); //get the colour depending on the rotation angle
        Shader smallCircleShader = getShader(density, rainbow);
        paint.setShader(smallCircleShader);
        canvas.drawPaint(paint);    //draw background

        Shader shader = new SweepGradient(0, getHeight() / 2, rainbow, null);   //add a shader with the rainbow colors

        //make the colour circle to rotate
        Matrix matrix = new Matrix(); //new matrix
        matrix.setRotate(myCoordinates.angle, 0, getHeight() / 2);    //rotate
        shader.setLocalMatrix(matrix);  //apply matrix on the shader

        paint.setShader(shader);    //apply shader on the paint
        canvas.drawCircle(0, getHeight() / 2, myCoordinates.circleSize * density, paint);  //show the shader at specific location only


        //paint.setShader(new Shader());
        //paint.setColor(oldColor); //get the colour depending on the rotation angle
        //canvas.drawPaint(paint);
        //canvas.drawCircle(0, getHeight()/2, 100*density, paint);
    }

    int oldColor = 0;
    @NonNull
    private Shader getShader(float density, int[] rainbow) {
        //make the middle circle to slowly change colours
        //Need to add all this code to a separate thread
        int newColor = getPixelColor((int) myCoordinates.angle, rainbow);
        int[] red = {red(oldColor), red(newColor)};
        int[] green = {green(oldColor), green(newColor)};
        int[] blue = {blue(oldColor), blue(newColor)};
        int redDiff = (red[1] - red[0]) / 16;
        int greenDiff = (green[1] - green[0]) / 16;
        int blueDiff = (blue[1] - blue[0]) / 16;
        oldColor = rgb(red[0] + redDiff, green[0] + greenDiff, blue[0] + blueDiff);
        int newerColor = rgb(red[0] + 2 * redDiff, green[0] + 2 * greenDiff, blue[0] + 2 * blueDiff);
        //Shader smallCircleShader = new RadialGradient(0, getHeight()/2, 100*density, oldColor, newerColor, Shader.TileMode.MIRROR);
        return new LinearGradient(0, getHeight() / 2 - 100 * density, 100, getHeight() / 2 + 100 * density, newerColor, oldColor, Shader.TileMode.MIRROR);
    }

    private int getPixelColor(int angle, int[] rainbow) {
        angle = -angle; //revert angle to increment it by going anti-clockwise
        //int[] rainbow = getRainbowColors(); //get the the colors
        int colorCount = rainbow.length - 1;    //get the number of colors excluding the last color which is the same as the first...
        //Log.e("Color Count is", colorCount + "");

        float stepDiff = (float) (360.0 / colorCount);    //get the angle between two complete colours
        //Log.e("Step Difference is", stepDiff + "");

        int[] r = new int[colorCount]; //create arrays to store single values of red/green/blue colors
        int[] g = new int[colorCount]; //for one rainbow color
        int[] b = new int[colorCount];

        for (int i = 0; i < colorCount; i++) {   //extract each color of the rainbow color
            //Log.e("red is", Color.red((rainbow[i])) + "");
            r[i] = red(rainbow[i]);
            g[i] = green(rainbow[i]);
            b[i] = blue((rainbow[i]));
        }

        while (angle < 0) {    //use only positive numbers for the angle
            angle += 360;
        }
        while (angle > 360) {    //use only numbers less than 360
            angle -= 360;
        }

        //Log.e("Angle is", angle + "");

        float[] redStep = new float[colorCount];   //step value per degree for each color
        float[] greenStep = new float[colorCount];
        float[] blueStep = new float[colorCount];

        for (int i = 0; i < colorCount; i++) {
            if (i == colorCount - 1) {
                //float redDiff =  ((r[0] - r[i])/stepDiff);
                //Log.e("Red diff is", redDiff + "");
                redStep[i] = (int) ((r[0] - r[i]) / stepDiff);
                greenStep[i] = (int) ((g[0] - g[i]) / stepDiff);
                blueStep[i] = (int) ((b[0] - b[i]) / stepDiff);
            } else {
                //float redDiff =  ((r[i+1] - r[i])/stepDiff);
                //Log.e("Red diff is", redDiff + "");
                redStep[i] = (int) ((r[i + 1] - r[i]) / stepDiff); //next color minus current color divided by the number of degrees between them
                greenStep[i] = (int) ((g[i + 1] - g[i]) / stepDiff);//later multiply by the angle to find the difference between real colour
                blueStep[i] = (int) ((b[i + 1] - b[i]) / stepDiff);//and the gradient colour
            }
        }

        int inBetween = 0;
        while (angle > stepDiff) { //find between which colours the angle is
            angle = (int) (angle - stepDiff);   //this also makes the angle to be between 0 and the stepDiff
            inBetween++;
        }
        if (inBetween > colorCount)     //sometimes a few degrees diff appears because float is not perfect...
            inBetween = colorCount;

        //Log.e("in Between these", inBetween + "");
        //Log.e("Angle is", angle+"");
        //int red = r[inBetween];
        //Log.e("red color factor is", "" + red);

        int color = rgb(r[inBetween] + (int) (angle * redStep[inBetween]),      //find the color depending on the stepSize and the angle
                g[inBetween] + (int) (angle * greenStep[inBetween]),           //original color + angle * incrementing step
                b[inBetween] + (int) (angle * blueStep[inBetween]));        //that's why angle had to be reverted as it was incrementing to the other side

        return color;
    }
}

