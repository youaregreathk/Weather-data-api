package com.lc.spring.service;

import com.lc.spring.model.WeatherDataModel;

import java.util.List;

public interface WeatherService {
    //public void save();

    WeatherDataModel getCurrentAndSaveWeatherByGPSCoordinates(double lat, double lon, String timeStamp);

    double getAvgTempByCoordinates(List<List<Double>> coordinates);

    List<Long> getHighTempByTime(String timeStamp);
}
