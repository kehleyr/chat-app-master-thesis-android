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

    public WeatherJSON getWeatherJSON() {
        return weatherJSON;
    }

    public void setWeatherJSON(WeatherJSON weatherJSON) {
        this.weatherJSON = weatherJSON;
    }

    WeatherJSON weatherJSON;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    String _id;

    public Distance getUsersDistance() {
        return usersDistance;
    }

    public void setUsersDistance(Distance usersDistance) {
        this.usersDistance = usersDistance;
    }

    Distance usersDistance;

    public GeoLocation getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(GeoLocation senderLocation) {
        this.senderLocation = senderLocation;
    }

    GeoLocation senderLocation;

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
