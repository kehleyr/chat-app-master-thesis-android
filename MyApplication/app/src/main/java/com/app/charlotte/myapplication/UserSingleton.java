package com.app.charlotte.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by charlotte on 01.05.16.
 */
public class UserSingleton {
    private String group;

    public User getCurrentUser(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (user==null)
        {
            String userName = sharedPref.getString(context.getString(R.string.edit_name_key), "");
           user =new User(userName, "");
        }


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


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
