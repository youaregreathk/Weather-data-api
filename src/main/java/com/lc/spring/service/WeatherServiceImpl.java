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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import static com.jayway.restassured.RestAssured.given;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String MAIN = "main";

    private static final String TEMP = "temp";

    private static final String HUMIDITY = "humidity";

    private static final String TEMP_MIN = "temp_min";

    private static final String TEMP_MAX = "temp_max";

    private static final String SYS = "sys";

    private static final String SUNRISE = "sunrise";

    private static final String SUNSET = "sunset";

    private static final String COUNTRY = "country";

    private static final String WEATHER = "weather";

    private static final String DESCRIPTION = "description";

    @Autowired
    private WeatherDataDAO weatherDataDAO;

    @Value("${weather.api.key}")
    private String apiId;

    @Value("${thread.concurrent.threshold}")
    private int threadThreshold;

    @Value("${temp.threshold}")
    private int highTempThreshold;

    @Override
    @Transactional
    public List<Long> getHighTempByTime(String timeStamp) {
        List<WeatherData> result = weatherDataDAO.getWeatherByTimeStamp(timeStamp);
        List<Long> highTempList = result.parallelStream()
                .filter(t -> t.getTemp() > highTempThreshold)
                .map(t -> t.getTemp())
                .collect(Collectors.toList());
        return highTempList;
    }

    @Override
    @Transactional
    public WeatherDataModel getCurrentAndSaveWeatherByGPSCoordinates(double lat, double lon, String timeStamp) {

        com.jayway.restassured.response.Response response = given().pathParam("apiId", apiId)
                .pathParam("lat", lat)
                .pathParam("lon", lon)
                .when().get("https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&APPID={apiId}");

        WeatherDataModel weatherDataModel = convertResponseObjectToWeatherDataModel(response, lat, lon, timeStamp);
        saveWeatherData(weatherDataModel);
        return weatherDataModel;
    }

    @Override
    @Transactional
    public double getAvgTempByCoordinates(List<List<Double>> coordinates) {

        String curTime = Util.getCurTime();
        coordinates.forEach(t -> {
            getCurrentAndSaveWeatherByGPSCoordinates(t.get(0), t.get(1), curTime);
        });

        AtomicLong atomicLong = new AtomicLong(0);
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadThreshold);
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
        try {
            JSONObject jsonObject  = new JSONObject(response.asString());

            Iterator<String> keys = jsonObject.keys();
            WeatherDataModel weatherDataModel = new WeatherDataModel();

            while (keys.hasNext()) {
                String key = keys.next();
                if (key.equals(MAIN)) {
                    JSONObject valueObject = jsonObject.getJSONObject(key);
                    weatherDataModel.setTemp(valueObject.getLong(TEMP));
                    weatherDataModel.setHumidity(valueObject.getLong(HUMIDITY));
                    weatherDataModel.setTempMin(valueObject.getLong(TEMP_MIN));
                    weatherDataModel.setTempMax(valueObject.getLong(TEMP_MAX));
                } else if (key.equals(SYS)) {
                    JSONObject valueObject = jsonObject.getJSONObject(key);
                    weatherDataModel.setSunRise(valueObject.getLong(SUNRISE));
                    weatherDataModel.setSunSet(valueObject.getLong(SUNSET));
                    weatherDataModel.setCountry(valueObject.getString(COUNTRY));
                } else if (key.equals(WEATHER)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(WEATHER);
                    JSONObject tmp = jsonArray.getJSONObject(0);
                    weatherDataModel.setWeatherDescription(tmp.getString(DESCRIPTION));
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

}
