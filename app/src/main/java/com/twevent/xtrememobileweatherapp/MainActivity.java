package com.twevent.xtrememobileweatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.twevent.xtrememobileweatherapp.forecast.WeatherForecastActivity;
import com.twevent.xtrememobileweatherapp.forecast.WeatherForecastResponseListener;
import com.twevent.xtrememobileweatherapp.forecast.tasks.AsyncWeatherForecastTask;
import com.twevent.xtrememobileweatherapp.search.SearchActivity;
import com.twevent.xtrememobileweatherapp.weather.AsyncCurrentWeatherTask;
import com.twevent.xtrememobileweatherapp.weather.CurrentWeatherResponseListener;
import com.twevent.xtrememobileweatherapp.weather.Weather;
import com.twevent.xtrememobileweatherapp.weather.WeatherPresenter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements WeatherForecastResponseListener, CurrentWeatherResponseListener {

    private static final int SEARCH_CODE = 9999;
    private static final String APP_PREFS = "app_prefs";
    private Weather currentWeather = null;
    private static final Map<String, Integer> weatherStatusImageMap;
    private SharedPreferences sharedPreferences;

    static {
        weatherStatusImageMap = new HashMap<>();
        weatherStatusImageMap.put("light rain", R.drawable.rainy_main);
        weatherStatusImageMap.put("moderate rain", R.drawable.sun);
        weatherStatusImageMap.put("clear sky", R.drawable.sunny_main);
        weatherStatusImageMap.put("sky is clear", R.drawable.sunny_main);
        weatherStatusImageMap.put("sky is clear", R.drawable.sunny_main);
        weatherStatusImageMap.put("broken clouds", R.drawable.cloud);
        weatherStatusImageMap.put("haze", R.drawable.cloud);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        loadWeatherFromFile();
        loadSharePreferences();
        View searchImageView = findViewById(R.id.searchImageView);
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearchActivity();
            }
        });
        setFavoriteButtonListener();
        boolean favoriteSaved = sharedPreferences.getBoolean("favoriteSaved",false);
        if(favoriteSaved) {
            showFavoriteSavedImage((ImageView) findViewById(R.id.favoriteSaved));
        }
    }

    private void loadSharePreferences() {
        sharedPreferences = getSharedPreferences(APP_PREFS, MODE_PRIVATE);
    }

    private void setFavoriteButtonListener() {
        ImageView favoriteButton = (ImageView) findViewById(R.id.favoriteSaved);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView view = (ImageView)v;
                boolean favoriteSaved = sharedPreferences.getBoolean("favoriteSaved",false);
                if(!favoriteSaved) {
                    saveFavorite();
                    showFavoriteSavedImage(view);
                } else {
                    removeFavorite();
                    showFavoriteNotSavedImage(view);
                }
            }
        });
    }

    private void showFavoriteNotSavedImage(ImageView view) {
        view.setImageResource(android.R.drawable.btn_star_big_off);
    }

    private void removeFavorite() {
        sharedPreferences.edit().putBoolean("favoriteSaved", false).apply();
    }

    private void showFavoriteSavedImage(ImageView view) {
        view.setImageResource(android.R.drawable.btn_star_big_on);
    }

    private void saveFavorite() {
        sharedPreferences.edit().putBoolean("favoriteSaved", true).apply();
    }

    private void loadWeatherFromFile() {
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("cityData.json");
            Gson gson = new Gson();
            Reader inputReader = new InputStreamReader(inputStream);
            currentWeather = gson.fromJson(inputReader, Weather.class);
            renderWeatherList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderWeatherList() {
        WeatherPresenter presenter = new WeatherPresenter(currentWeather);
        TextView cityName = (TextView) findViewById(R.id.cityName);
        cityName.setText(currentWeather.getName());

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(presenter.getDayOfTheWeek());

        TextView temperature = (TextView) findViewById(R.id.temperature);
        temperature.setText(presenter.getTemperatureInCelsius());

        TextView weatherDescription = (TextView) findViewById(R.id.weatherDescription);
        weatherDescription.setText(currentWeather.getWeather().get(0).getDescription());

        ImageView weatherImage = (ImageView) findViewById(R.id.weatherImage);
        int imageResource = R.drawable.sunny_main;
        if (weatherStatusImageMap.containsKey(currentWeather.getWeather().get(0).getDescription())) {
            imageResource = weatherStatusImageMap.get(currentWeather.getWeather().get(0).getDescription());
        }
        weatherImage.setImageResource(imageResource);    }

    public void showForecastDetails(View view) {
        fetchForecastData();
    }

    private void fetchForecastData(){
        String weatherUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + currentWeather.getName() + "&units=metric" + "&" + "APPID=" + "ebbc66b823072502c81339f5b0b9b042";
        AsyncWeatherForecastTask task = new AsyncWeatherForecastTask(this);
        task.execute(weatherUrl);
    }

    @Override
    public void weatherForecastReceived(String data) {
        Intent intent = new Intent(this, WeatherForecastActivity.class);
        intent.putExtra("weatherData", data);
        intent.putExtra("city", currentWeather.getName());
        startActivity(intent);
    }

    @Override
    public void weatherForecastFailed() {
        Toast.makeText(this, "An error occurred. Please try again", Toast.LENGTH_LONG).show();
    }

    private void launchSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_CODE && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("latitude", -1);
            double longitude = data.getDoubleExtra("longitude", -1);
            if (latitude != -1 && longitude != -1) {
                fetchWeatherForLocation(latitude, longitude);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchWeatherForLocation(double latitude, double longitude) {
        AsyncCurrentWeatherTask task = new AsyncCurrentWeatherTask(this);
        task.execute(latitude, longitude);
    }

    @Override
    public void onCurrentWeatherFetched(Weather weather) {
        currentWeather = weather;
        renderWeatherList();
    }

    @Override
    public void onCurrentWeatherFetchFailure() {
        Toast.makeText(MainActivity.this, "An error occurred. Please try again", Toast.LENGTH_LONG).show();
    }
}
