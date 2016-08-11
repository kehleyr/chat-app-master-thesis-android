package com.app.charlotte.myapplication.weather;

import com.app.charlotte.myapplication.WeatherJSON;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by charlotte on 31.05.16.
 */
public interface OpenWeatherService {

    @GET("weather")
    Call<WeatherJSON> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String appId, @Query("units") String units);
}
