package com.example.charlotte.myapplication;

/**
 * Created by charlotte on 01.05.16.
 */
public class UserSingleton {
    public User getCurrentUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static UserSingleton getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(UserSingleton ourInstance) {
        UserSingleton.ourInstance = ourInstance;
    }

    User user;

    private static UserSingleton ourInstance = new UserSingleton();

    public static UserSingleton getInstance() {
        return ourInstance;
    }

    private UserSingleton() {
    }
}