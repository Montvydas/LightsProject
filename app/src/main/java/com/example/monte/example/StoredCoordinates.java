package com.example.monte.example;

/**
 * Created by monte on 31/08/2015.
 */
public class StoredCoordinates {
    public float X = 0, Y = 0, prevX = 0, prevY = 0, angleSpeed = 0, angle = 0, circleSize = 200;
    public boolean isPressed = false;

    public final static StoredCoordinates INSTANCE = new StoredCoordinates();

    private StoredCoordinates(){

    }
}
