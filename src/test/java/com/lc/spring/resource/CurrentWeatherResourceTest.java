package com.lc.spring.resource;

import com.lc.spring.model.WeatherDataModel;
import com.lc.spring.model.response.WeatherDataResponse;
import com.lc.spring.service.WeatherService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CurrentWeatherResourceTest {

    @Mock
    WeatherService weatherService;

    @InjectMocks
    private CurrentWeatherResource resource;

    @Before
    public void setUp() {
        resource = new CurrentWeatherResource();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCurrentWeather() {

        WeatherDataModel weatherDataModel = WeatherDataModel.builder()
                .weatherDescription("Sunny")
                .country("US")
                .humidity(123)
                .sunRise(1234)
                .sunSet(3456)
                .temp(278)
                .tempMax(290)
                .tempMin(220)
                .build();

        when(weatherService.getCurrentAndSaveWeatherByGPSCoordinates(any(Double.class), any(Double.class),
                any(String.class))).thenReturn(weatherDataModel);
        Response response = resource.getCurrentWeatherByGPSCoord(12.35, -122.56);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        WeatherDataModel result = (WeatherDataModel)((WeatherDataResponse) response.getEntity()).getContent();
        assertEquals("US", result.getCountry());
        assertEquals("Sunny", result.getWeatherDescription());
        assertEquals(278, result.getTemp());
        assertEquals(290, result.getTempMax());
    }

}
