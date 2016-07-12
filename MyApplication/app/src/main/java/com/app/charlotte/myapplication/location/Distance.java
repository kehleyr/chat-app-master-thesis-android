package com.app.charlotte.myapplication.location;

/**
 * Created by charlotte on 19.05.16.
 */
public class Distance {

    public Distance(float distanceInMeters) {
        this.distanceValue=distanceInMeters;

    }

    public float getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(float distanceValue) {
        this.distanceValue = distanceValue;
    }

    private float distanceValue;
}
