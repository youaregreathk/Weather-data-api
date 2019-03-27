package com.lc.spring.service;

import com.lc.spring.dao.WeatherDataDAO;
import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;
import com.lc.spring.model.WeatherDataModel;

import com.uber.h3core.util.GeoCoord;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.transaction.annotation.Transactional;

import static com.jayway.restassured.RestAssured.given;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String APIID = "18e98cbef3b853720c15a28f2eb2a960";

    @Autowired
    private WeatherDataDAO weatherDataDAO;

    @Override
    @Transactional
    public double getAvgTempByCoordinates(List<List<Double>> coordinates) {
        List<String> goeCoordList = Util.convertCoordListToGeoList(coordinates);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String curTime  = simpleDateFormat.format(new Date());

        coordinates.forEach(t -> {
            try {
                getCurrentAndSaveWeatherByGPSCoordinates(t.get(0), t.get(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        AtomicLong atomicLong = new AtomicLong(0);
        coordinates.parallelStream()
                .forEach((t) -> {
                    logger.info("Before Getting");
                    String geoCoordinate = Util.convertCoordToGeoCoord(t.get(0), t.get(1));
                    logger.info(geoCoordinate);
                    atomicLong.updateAndGet(n -> n + getWeatherByGPSCoordinates(
                            new WeatherId(geoCoordinate, curTime)).getTemp());

                    logger.info(String.valueOf(atomicLong.longValue()));
                });
        return (9/5 * ((atomicLong.longValue()/ coordinates.size()) - 273.15) + 32) ;
    }

    @Async
    @Transactional
    public WeatherDataModel getWeatherByGPSCoordinates(WeatherId weatherId) {

        WeatherData weatherData = weatherDataDAO.get(weatherId);
        List<GeoCoord> geoCoords = Util.convertGeoCoorToCoord(weatherId.getGeoCoordinate());
        logger.info("lat " + geoCoords.get(0).toString());
        logger.info("lon " + geoCoords.get(1).toString());

        long lat = (long) geoCoords.get(0).lat;
        long lon = (long) geoCoords.get(1).lng;

        logger.info("lat " + lat);
        logger.info("lon " + lon);

        WeatherDataModel weatherDataModel = WeatherDataModel.builder()
                .weatherDescription(weatherData.getWeatherDescription())
                .country(weatherData.getCountry())
                .humidity(weatherData.getHumidity())
                .sunRise(weatherData.getSunRise())
                .sunSet(weatherData.getSunSet())
                .temp(weatherData.getTemp())
                .tempMax(weatherData.getTempMax())
                .tempMin(weatherData.getTempMin())
                .timeStamp(weatherData.getWeatherId().getTimeStamp())
                .latitude(lat)
                .longitude(lon)
                .build();
        return weatherDataModel;
    }


    @Override
    @Transactional
    public WeatherDataModel getCurrentAndSaveWeatherByGPSCoordinates(double lat, double lon) throws org.json.JSONException {

        com.jayway.restassured.response.Response response = given().pathParam("APIID", APIID)
                .pathParam("lat", lat)
                .pathParam("lon", lon)
                .when().get("https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&APPID={APIID}");

        WeatherDataModel weatherDataModel = convertResponseObjectToWeatherDataModel(response, lat, lon);

        logger.info("Before Saving");

        saveWeatherData(weatherDataModel);

        logger.info("After Saving");
        return weatherDataModel;
    }

    public void saveWeatherData(WeatherDataModel weatherDataModel) {
        String geoCoord = Util.convertCoordToGeoCoord(weatherDataModel.getLatitude(),
                weatherDataModel.getLongitude());

        WeatherId weatherId = WeatherId.builder()
                .timeStamp(weatherDataModel.getTimeStamp())
                .geoCoordinate(geoCoord)
                .build();

        WeatherData weatherData = WeatherData.builder()
                .weatherId(weatherId)
                .weatherDescription(weatherDataModel.getWeatherDescription())
                .country(weatherDataModel.getCountry())
                .humidity(weatherDataModel.getHumidity())
                .temp(weatherDataModel.getTemp())
                .tempMin(weatherDataModel.getTempMin())
                .tempMax(weatherDataModel.getTempMax())
                .sunRise(weatherDataModel.getSunRise())
                .sunSet(weatherDataModel.getSunSet())
                .build();

        weatherDataDAO.save(weatherData);
    }

    private WeatherDataModel convertResponseObjectToWeatherDataModel(com.jayway.restassured.response.Response response, double lat, double lon)
            throws org.json.JSONException {
        JSONObject jsonObject = new JSONObject(response.asString());
        Iterator<String> keys = jsonObject.keys();
        WeatherDataModel weatherDataModel = new WeatherDataModel();

        while(keys.hasNext()) {
            String key = keys.next();
            if(key.equals("main")) {
                JSONObject valueObject = jsonObject.getJSONObject(key);
                weatherDataModel.setTemp(valueObject.getLong("temp"));
                weatherDataModel.setHumidity(valueObject.getLong("humidity"));
                weatherDataModel.setTempMin(valueObject.getLong("temp_min"));
                weatherDataModel.setTempMax(valueObject.getLong("temp_max"));
            } else if(key.equals("sys")) {
                JSONObject valueObject = jsonObject.getJSONObject(key);
                weatherDataModel.setSunRise(valueObject.getLong("sunrise"));
                weatherDataModel.setSunSet(valueObject.getLong("sunset"));
                weatherDataModel.setCountry(valueObject.getString("country"));
            } else if(key.equals("weather")) {
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject tmp= jsonArray.getJSONObject(0);
                weatherDataModel.setWeatherDescription(tmp.getString("description"));
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String curTime  = simpleDateFormat.format(new Date());
        weatherDataModel.setTimeStamp(curTime);
        weatherDataModel.setLatitude(lat);
        weatherDataModel.setLongitude(lon);
        return weatherDataModel;
    }

    @Override
    @Transactional
    public void save() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String curTime  = simpleDateFormat.format(new Date());

        WeatherId weatherId = WeatherId.builder()
                .geoCoordinate("12344")
                .timeStamp(curTime)
                .build();

        WeatherData weatherData = WeatherData.builder()
                .weatherId(weatherId)
                .sunSet(new Long(1234))
                .tempMax(new Long(1234))
                .tempMin(new Long(1234))
                .temp(new Long(1234))
                .sunRise(new Long(1234))
                .humidity(new Long(1234))
                .country("US")
                .weatherDescription("Sunny")
                .build();
        weatherDataDAO.save(weatherData);
    }
}
