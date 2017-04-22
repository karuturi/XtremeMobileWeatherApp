package com.twevent.xtrememobileweatherapp.listeners;

/**
 * Created by rajani on 22/04/17.
 */

public interface WeatherResponseListener {
    void weatherForecastFailed();
    void weatherForecastReceived(String weatherData);
}
