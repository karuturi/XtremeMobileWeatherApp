package com.twevent.xtrememobileweatherapp.asynctasks;

import android.os.AsyncTask;

import com.twevent.xtrememobileweatherapp.listeners.WeatherResponseListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rajani on 22/04/17.
 */

public class AsyncWeatherForecastTask extends AsyncTask<String, Void, String> {

    private static final String FAILED_EVENT = "FailedEvent";
    private final WeatherResponseListener listener;

    public AsyncWeatherForecastTask(WeatherResponseListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urls[0]).build();
        Call newCall = client.newCall(request);
        try {
            Response response = newCall.execute();
            if(response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILED_EVENT;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(FAILED_EVENT.equals(s)) {
            listener.weatherForecastFailed();
        } else {
            listener.weatherForecastReceived(s);
        }
    }
}
