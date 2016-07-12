package com.app.charlotte.myapplication.audio;

/**
 * Created by charlotte on 12.05.16.
 */
public class AmbientNoise {


    public AmbientNoise(double decibels) {
        this.decibels = decibels;
    }

    public double getDecibels() {
        return decibels;
    }

    public void setDecibels(long decibels) {
        this.decibels = decibels;
    }

    @Override
    public String toString() {
        return "AmbientNoise{" +
                "decibels=" + decibels +
                '}';
    }

    double decibels;
}
