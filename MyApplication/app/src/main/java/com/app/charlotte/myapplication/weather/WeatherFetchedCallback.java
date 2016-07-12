package com.app.charlotte.myapplication.weather;

import com.app.charlotte.myapplication.WeatherJSON;

/**
 * Created by charlotte on 31.05.16.
 */
public interface WeatherFetchedCallback {

    public void onWeatherFetched(WeatherJSON weatherJSON);
}
