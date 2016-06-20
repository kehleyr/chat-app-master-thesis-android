package com.example.charlotte.myapplication;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by charlotte on 31.05.16.
 */
public class WeatherHelper {
    private static WeatherHelper ourInstance = new WeatherHelper();
    private static final String openWeatherImageURL= "http://openweathermap.org/img/w/";
    private static final String pngSuffix=".png";
    private static final String weatherAPIKey="82638158e56316d1e162d9cf4463c086";

    public static WeatherHelper getInstance() {
        return ourInstance;
    }

    private WeatherHelper() {
    }


    public String getURLForIconString(String iconString)
    {

        return openWeatherImageURL+iconString+pngSuffix;

    }

    public void getWeather(GeoLocation location, final WeatherFetchedCallback weatherFetchedCallback){




        Call<WeatherJSON> call = Application.getWeatherService().getWeather(location.getLatitude(), location.getLongitude(), weatherAPIKey, "metric");

        Log.d("TAG", " call: "+ call.request().toString());
        call.enqueue(new Callback<WeatherJSON>() {
            @Override
            public void onResponse(Call<WeatherJSON> call, Response<WeatherJSON> response) {
                weatherFetchedCallback.onWeatherFetched(response.body());

            }

            @Override
            public void onFailure(Call<WeatherJSON> call, Throwable t) {
                weatherFetchedCallback.onWeatherFetched(null);

            }
        });




    }
}
