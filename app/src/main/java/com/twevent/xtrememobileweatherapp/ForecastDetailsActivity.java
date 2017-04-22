package com.twevent.xtrememobileweatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ForecastDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_details);
        String cityName = getIntent().getStringExtra("cityName");
        TextView cityTextView = (TextView) findViewById(R.id.detailsCityName);
        cityTextView.setText(cityName);
    }
}
