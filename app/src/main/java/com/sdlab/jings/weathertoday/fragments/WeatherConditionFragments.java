package com.sdlab.jings.weathertoday.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdlab.jings.weathertoday.data.Condition;

import offline_1.sdlab.com.weathertoday.R;

import com.sdlab.jings.weathertoday.data.Units;

public class WeatherConditionFragments extends Fragment {
    private ImageView weatherIconImageViewf;
    private TextView dayLabelTextView;
    private TextView highTemperatureTextView;
    private TextView lowTemperatureTextView;
    private TextView descriptionDay;
    private TextView date;

    public WeatherConditionFragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_condition, container, false);

        weatherIconImageViewf = (ImageView) view.findViewById(R.id.weatherIconImageView);
        dayLabelTextView = (TextView) view.findViewById(R.id.dayTextView);
        highTemperatureTextView = (TextView) view.findViewById(R.id.highTemperatureTextView);
        lowTemperatureTextView = (TextView) view.findViewById(R.id.lowTemperatureTextView);
        descriptionDay = (TextView) view.findViewById(R.id.descriptionDay);
        date = (TextView) view.findViewById(R.id.date);

        return view;
    }

    public void loadForecast(Condition forecast, Units units) {
        int weatherIconImageResource = getResources().getIdentifier("icon_" + forecast.getCode(), "drawable", getActivity().getPackageName());

        weatherIconImageViewf.setImageResource(weatherIconImageResource);
        dayLabelTextView.setText(forecast.getDay());
        highTemperatureTextView.setText(getString(R.string.temperature_output, forecast.getHighTemperature(), units.getTemperature()));
        lowTemperatureTextView.setText(getString(R.string.temperature_output, forecast.getLowTemperature(), units.getTemperature()));
        descriptionDay.setText(forecast.getDescription());
        date.setText(forecast.getDate());


    }


}

