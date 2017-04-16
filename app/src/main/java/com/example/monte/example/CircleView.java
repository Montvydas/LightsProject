package com.example.monte.example;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Color.*;

/**
 * Created by monte on 26/08/2015.
 */
public class CircleView extends View implements View.OnTouchListener{
    private float horizontalOrientation = 0.5f;
    private float verticalOrientation = 0.5f;
    private int circleSize = 200;
    private CircleViewListener circleViewListener;

    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
    }

    public void setHorizontalOrientation(float horizontalOrientation) {
        this.horizontalOrientation = horizontalOrientation;
    }

    public void setVerticalOrientation(float verticalOrientation) {
        this.verticalOrientation = verticalOrientation;
    }

    private Paint circlePaint = new Paint();
//    private Shader circleShader;

    public void setupCircle (int[] colors){
//        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);           //fill with no borders
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);      //anti aliasing filter
        setOnTouchListener(this);
//        circleSize = getHeight() / 2;

//        circleShader = new SweepGradient(getWidth() * horizontalOrientation,
//                getHeight() * verticalOrientation, colors, null);   //add a shader with the rainbow colors
    }

    public static int[] rainbowColors = new int[]{
            RED,
            YELLOW,
            GREEN,
            CYAN,
            BLUE,
            MAGENTA,
            RED   //Need the first color to be the same as the last color for gradient to be even at all places};
    };

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    float angleSpeed = 0;
    float angle = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

//        Log.e("sizes", "Left=" + getLeft() + " Right=" + getRight() + " Top=" + getTop() + " Bottom=" + getBottom());
//        Log.e("Pressed Coordinates", prevX + " " + prevY);

        float fingerAngle = (float) Math.atan2(-(currY - getHeight()*verticalOrientation),
                currX - getWidth()*horizontalOrientation);
//        Log.e("finger Angle", fingerAngle + " degrees");

        if (isPressed == false) {
            angleSpeed *= 0.95;
            if (angleSpeed < 0.2 && angleSpeed > -0.2)
                angleSpeed = 0;
        } else {
            // check currY vs prevY instead
//            angleSpeed = (float) ((prevY - currY) * 0.2);


            angleSpeed = (float) ((Math.cos(fingerAngle)*(prevY - currY) + Math.sin(fingerAngle)*(prevX-currX)));
            if (angleSpeed < 1 && angleSpeed > -1)
                angleSpeed *= 0.0;
            else
                angleSpeed *= 0.3;
//                angleSpeed = (float) Math.pow(angleSpeed, 5.0);
        }

        //get the pixel density
        float density = getResources().getDisplayMetrics().density;
        //Log.e("AngleSpeed is", angleSpeed + "");

//        angleSpeed = (float) ((prevY - currY) * 0.4);
        angle -= angleSpeed;     //apply a specific multiplier to make the rotation speed to correspond to the scrolling speed

//        int[] rainbow = rainbowColors; //get the colours to fill in the circle
//        paint.setColor(getPixelColor((int) angle, rainbowColors)); //get the colour depending on the rotation angle

        if (circleViewListener != null && Math.abs(angleSpeed) > 0){
            circleViewListener.onColorChange(getPixelColor((int) angle, rainbowColors));
        }

//        Shader smallCircleShader = getShader(density, rainbow);
//        paint.setShader(smallCircleShader);
//        canvas.drawPaint(paint);    //draw background

        Shader circleShader = new SweepGradient(getWidth() * horizontalOrientation,
                getHeight() * verticalOrientation, rainbowColors, null);   //add a shader with the rainbow colors

        //make the colour circle to rotate
        Matrix matrix = new Matrix(); //new matrix
        matrix.setRotate(angle, getWidth() * horizontalOrientation,
                getHeight() * verticalOrientation);    //rotate
        circleShader.setLocalMatrix(matrix);  //apply matrix on the shader

        circlePaint.setShader(circleShader);    //apply shader on the paint
//        int circleSize = getHeight() > getWidth() ? getWidth() : getHeight();

//        canvas.drawPaint(circlePaint);
        canvas.drawCircle(getWidth() * horizontalOrientation,
                getHeight() * verticalOrientation, getHeight() / 2, circlePaint);  //show the shader at specific location only

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
        int newColor = getPixelColor((int) angle, rainbow);
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

    private float currX, currY, prevX, prevY;
    private boolean isPressed = false;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float distance = (float) Math.sqrt(Math.pow(motionEvent.getX() - getWidth()*horizontalOrientation, 2) +
                Math.pow(motionEvent.getY() - getHeight()*verticalOrientation, 2));
        if (distance > getHeight() / 2){
            isPressed = false;
            return true;
        }
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:   //prev value now becomes equal to current value
                currX = motionEvent.getX();
                currY = motionEvent.getY();
                prevY = currY;
                prevX = currX;
                isPressed = true;
                // if (X < myCoordinates.circleSize) {
//                setMousePosition(X, Y, X, Y, true);

                //}
                break;
            case MotionEvent.ACTION_MOVE:   //prev value is different than current value
                prevY = currY;
                prevX = currX;
                currX = motionEvent.getX();
                currY = motionEvent.getY();
                isPressed = true;

                //if (X < myCoordinates.circleSize) {

//                setMousePosition(X, Y, prevX, prevY, true);
                //}
                break;
            case MotionEvent.ACTION_UP: //to make post-touch scroll effect
                isPressed = false;
                break;
        }
        return true;
    }

    public void setCircleViewListener(CircleViewListener circleViewListener) {
        this.circleViewListener = circleViewListener;
    }

    public interface CircleViewListener {
        void onColorChange (int color);
    }
}

