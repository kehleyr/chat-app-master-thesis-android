package com.app.charlotte.myapplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.app.charlotte.myapplication.spotify.MySpotifyService;
import com.app.charlotte.myapplication.spotify.SpotifyServiceSingleton;
import com.app.charlotte.myapplication.weather.OpenWeatherService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by charlotte on 18.04.16.
 */
public class Application extends android.app.Application {


    private static boolean inSingleConversationActivity;
    private static String currentUsername;

    public static Retrofit getMessageRetrofit() {
        return messageRetrofit;
    }

    private static Retrofit messageRetrofit;

    public static MessagingServerService getService() {
        return service;
    }

    private static MessagingServerService service;

    public static OpenWeatherService getWeatherService() {
        return weatherService;
    }

    private static OpenWeatherService weatherService;

    public static MySpotifyService getSpotifyService() {
        return spotifyService;
    }

    public static void setSpotifyService(MySpotifyService spotifyService) {
        Application.spotifyService = spotifyService;
    }

    private static MySpotifyService spotifyService;
    static String baseURL = "https://immense-earth-44435.herokuapp.com/";

    public static boolean isInSingleConversationActivity() {
        return inSingleConversationActivity;
    }

    public static void setInSingleConversationActivity(boolean inSingleConversationActivity) {
        Application.inSingleConversationActivity = inSingleConversationActivity;
    }

    public static void setCurrentUsername(String currentUsername) {
        Application.currentUsername = currentUsername;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                .create();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String url = sharedPref.getString(getString(R.string.server_string),baseURL);
        Log.d("Application", url);


        try {

            messageRetrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))

                    .build()
            ;
        }catch(IllegalArgumentException e)
        {
            messageRetrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))

                    .build();

        }

        service = messageRetrofit.create(MessagingServerService.class);

/*
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
*/


        Retrofit spotifyRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
               // .client(client)
                .build();


        spotifyService=spotifyRetrofit.create(MySpotifyService.class);

        Retrofit weatherRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                        // .client(client)
                .build();

        weatherService=weatherRetrofit.create(OpenWeatherService.class);

        SpotifyServiceSingleton.getInstance().initialize();



        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)

                .showImageOnLoading(R.drawable.music_note)
                .considerExifParams(true)
                .build();



        //Create a config with those options.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);

    }


}
