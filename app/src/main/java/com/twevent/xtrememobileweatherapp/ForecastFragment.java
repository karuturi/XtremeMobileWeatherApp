package com.twevent.xtrememobileweatherapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {


    String[] cities = {"Hyderabad", "Bangalore"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_forecast, container, false);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout
                .list_view_adapter, cities);
        ListView listView = (ListView) view.findViewById(R.id.forecast_listview);
        listView.setAdapter(arrayAdapter);
        return view;
    }

}
