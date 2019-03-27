package com.lc.spring.dao;

import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;

public interface WeatherDataDAO {

    void save(WeatherData weatherData);

    WeatherData get(WeatherId weatherId);
}