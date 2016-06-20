package com.example.charlotte.myapplication;

import java.util.List;

/**
 * Created by charlotte on 31.05.16.
 */
public class WeatherJSON {


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    Main main;

    public List<Weather> getWeatherList() {
        return weather;
    }

    public void setWeatherList(List<Weather> weatherList) {
        this.weather = weatherList;
    }

    List<Weather> weather;

    public Double getTemperature(){

        return getMain().getTemp();
    }



}

class Main {
    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    private Double temp;

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    private Double pressure;
    private Integer humidity;


}

class Weather{

    String main;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    String description;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    String icon;


}