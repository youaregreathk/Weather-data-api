package com.lc.spring.repository;

import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;

import java.util.List;

public interface WeatherDataDAO {

    void save(WeatherData weatherData);

    WeatherData get(WeatherId weatherId);

    List<WeatherData> getWeatherByTimeStamp(String timeStamp);
}