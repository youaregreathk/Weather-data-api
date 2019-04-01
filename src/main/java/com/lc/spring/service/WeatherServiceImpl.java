package com.lc.spring.service;

import com.lc.spring.exception.WdDataFormatException;
import com.lc.spring.exception.WdRemoteException;
import com.lc.spring.repository.WeatherDataDAO;
import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;
import com.lc.spring.model.WeatherDataModel;

import com.uber.h3core.util.GeoCoord;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import static com.jayway.restassured.RestAssured.given;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final double HIGH_TEMP_THERSHOLD = 281;

    private static final int THREAD_THERSHOLD = 120;

    @Autowired
    private WeatherDataDAO weatherDataDAO;

    @Value("${weather.api.key}")
    private String APIID;

    @Override
    @Transactional
    public double getAvgTempByCoordinates(List<List<Double>> coordinates) {

        String curTime = Util.getCurTime();
        coordinates.forEach(t -> {
            getCurrentAndSaveWeatherByGPSCoordinates(t.get(0), t.get(1), curTime);
        });

        AtomicLong atomicLong = new AtomicLong(0);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_THERSHOLD);
        try {
            forkJoinPool.submit(() -> coordinates.parallelStream()
                    .forEach((t) -> {
                        String geoCoordinate = Util.convertCoordToGeoCoord(t.get(0), t.get(1));
                        atomicLong.updateAndGet(n -> n + getWeatherByGPSCoordinates(
                                new WeatherId(geoCoordinate, curTime)).getTemp());
                    })).get();

        } catch (ExecutionException e) {
            logger.error("Exception running parallel getting weather data from the database");
            throw new WdRemoteException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Exception running parallel getting weather data from the database");
            throw new WdRemoteException(e.getMessage());
        }
        return Util.getAvergeTempInFahrenheit(atomicLong.longValue(), coordinates.size());
    }

    @Override
    @Transactional
    public List<Long> getHighTempByTime(String timeStamp) {
        List<WeatherData> result = weatherDataDAO.getWeatherByTimeStamp(timeStamp);
        List<Long> highTempList = result.parallelStream()
                .filter(t -> t.getTemp() > HIGH_TEMP_THERSHOLD)
                .map(t -> t.getTemp())
                .collect(Collectors.toList());
        return highTempList;
    }

    @Transactional
    public WeatherDataModel getWeatherByGPSCoordinates(WeatherId weatherId) {

        WeatherData weatherData = weatherDataDAO.get(weatherId);
        List<GeoCoord> geoCoords = Util.convertGeoCoorToCoord(weatherId.getGeoCoordinate());

        long lat = (long) geoCoords.get(0).lat;
        long lon = (long) geoCoords.get(1).lng;

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
    public WeatherDataModel getCurrentAndSaveWeatherByGPSCoordinates(double lat, double lon, String timeStamp) {

        com.jayway.restassured.response.Response response = given().pathParam("APIID", APIID)
                .pathParam("lat", lat)
                .pathParam("lon", lon)
                .when().get("https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&APPID={APIID}");

        WeatherDataModel weatherDataModel = convertResponseObjectToWeatherDataModel(response, lat, lon, timeStamp);
        saveWeatherData(weatherDataModel);
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

    private WeatherDataModel convertResponseObjectToWeatherDataModel(com.jayway.restassured.response.Response response,
                                                                     double lat, double lon, String curTime) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.asString());

            Iterator<String> keys = jsonObject.keys();
            WeatherDataModel weatherDataModel = new WeatherDataModel();

            while (keys.hasNext()) {
                String key = keys.next();
                if (key.equals("main")) {
                    JSONObject valueObject = jsonObject.getJSONObject(key);
                    weatherDataModel.setTemp(valueObject.getLong("temp"));
                    weatherDataModel.setHumidity(valueObject.getLong("humidity"));
                    weatherDataModel.setTempMin(valueObject.getLong("temp_min"));
                    weatherDataModel.setTempMax(valueObject.getLong("temp_max"));
                } else if (key.equals("sys")) {
                    JSONObject valueObject = jsonObject.getJSONObject(key);
                    weatherDataModel.setSunRise(valueObject.getLong("sunrise"));
                    weatherDataModel.setSunSet(valueObject.getLong("sunset"));
                    weatherDataModel.setCountry(valueObject.getString("country"));
                } else if (key.equals("weather")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject tmp = jsonArray.getJSONObject(0);
                    weatherDataModel.setWeatherDescription(tmp.getString("description"));
                }
            }

            weatherDataModel.setTimeStamp(curTime);
            weatherDataModel.setLatitude(lat);
            weatherDataModel.setLongitude(lon);
            return weatherDataModel;
        } catch (JSONException e) {
            logger.error("Exception getting weather data from the database");
            throw new WdDataFormatException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void save() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String curTime = simpleDateFormat.format(new Date());

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
