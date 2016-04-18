package com.example.charlotte.myapplication;

import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by charlotte on 18.04.16.
 */
public class Application extends android.app.Application {


    public static MessagingServerService getService() {
        return service;
    }

    private static MessagingServerService service;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://localhost")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MessagingServerService.class);
    }
}
