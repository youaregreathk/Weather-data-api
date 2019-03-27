package com.lc.spring.service;

import com.lc.spring.model.WeatherDataModel;

import java.util.List;

public interface WeatherService {
    public void save();

    public WeatherDataModel getCurrentAndSaveWeatherByGPSCoordinates(double lat, double lon) throws org.json.JSONException;

    public double getAvgTempByCoordinates(List<List<Double>> coordinates);
}
