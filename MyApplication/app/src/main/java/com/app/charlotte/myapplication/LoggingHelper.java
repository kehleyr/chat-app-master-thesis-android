package com.app.charlotte.myapplication;

import android.util.Log;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by charlotte on 05.08.16.
 */
public class LoggingHelper {
    private static LoggingHelper ourInstance = new LoggingHelper();

    public static final String OPEN_MESSAGE_EVENT="openMessageEvent";
    public static final String CLOSE_MESSAGE_EVENT="closeMessageEvent";
    public static final String OPEN_MESSAGE_DETAILS_EVENT="openMessageDetailsEvent";
    public static final String CLOSE_MESSAGE_DETAILS_EVENT="closeMessageDetailsEvent";
    public static final String START_PLAYING_SONG="startPlayingSong";
    public static final String STOP_PLAYING_SONG="stopPlayingSong";

    public static LoggingHelper getInstance() {
        return ourInstance;
    }

    private LoggingHelper() {
    }



    public void logEvent(String event, String username)
    {

        Call<Result> call = Application.getService().addEvent(username, new Date(), event);
        Log.d("TAG", "initialize adapter again");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Log.d("LoggingHelper", "sent log successfully!");

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                Log.e("LoggingHelper", "Logging failed");

            }
        });

    }
}
