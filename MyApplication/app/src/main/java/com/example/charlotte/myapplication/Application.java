package com.example.charlotte.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

    public static MySpotifyService getSpotifyService() {
        return spotifyService;
    }

    public static void setSpotifyService(MySpotifyService spotifyService) {
        Application.spotifyService = spotifyService;
    }

    private static MySpotifyService spotifyService;
    static String baseURL = "http://10.176.89.145:3000/";
    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
               ;

        service = retrofit.create(MessagingServerService.class);

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
