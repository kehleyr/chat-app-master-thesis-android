package com.example.charlotte.myapplication;

import android.location.Location;

import java.util.Date;

/**
 * Created by charlotte on 01.05.16.
 */
public class Message {


    Date timestamp;
    private String fromDisplayName;
    Location fromUserLocation;
    String fromUserName, toUserName;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;

    public SingleShotLocationProvider.GPSCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(SingleShotLocationProvider.GPSCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    SingleShotLocationProvider.GPSCoordinates coordinates;

    public AmbientNoise getAmbientNoise() {
        return ambientNoise;
    }

    public void setAmbientNoise(AmbientNoise ambientNoise) {
        this.ambientNoise = ambientNoise;
    }

    AmbientNoise ambientNoise;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    private Song song;

    public Message(String fromUserName, String toUserName, String messageText) {
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
        this.messageText = messageText;
    }

    String messageText;


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFromUser() {
        return fromUserName;
    }

    public void setFromUser(String fromUser) {
        this.fromUserName = fromUser;
    }

    public String getToUser() {
        return toUserName;
    }

    public void setToUser(String toUser) {
        this.toUserName = toUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


    public String getFromUserDisplayName() {
        return fromDisplayName;
    }
}
