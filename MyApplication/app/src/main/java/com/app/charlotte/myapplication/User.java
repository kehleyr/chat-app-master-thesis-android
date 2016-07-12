package com.app.charlotte.myapplication;

/**
 * Created by charlotte on 01.05.16.
 */
public class User {

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    String username;
    String displayName;

    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }




    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
