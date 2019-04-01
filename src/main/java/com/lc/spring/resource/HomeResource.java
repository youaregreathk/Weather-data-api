package com.lc.spring.resource;

import com.lc.spring.model.WeatherDataModel;
import com.lc.spring.model.response.WeatherDataResponse;
import com.lc.spring.service.Util;
import com.lc.spring.service.WeatherService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class HomeResource {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WeatherService weatherService;

    @RequestMapping(value = "/weather/{latitude}/coordinates/{longitude}/current", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Security error"),
            @ApiResponse(code = 500, message = "Unexpected failure"),
            @ApiResponse(code = 503, message = "Service unavailable")})
    public Response getCurrentWeatherByGPSCoord(@PathVariable(value = "latitude", required = true) double lat,
                                                @PathVariable(value = "longitude", required = true) double lon
    ) throws JSONException {

        logger.info("START:getCurrentWeatherByGPSCoord for lat={}, lon={}", lat, lon);

        String curTime = Util.getCurTime();
        WeatherDataModel weatherDataModel = weatherService.getCurrentAndSaveWeatherByGPSCoordinates(lat , lon, curTime);

        logger.info("END:getCurrentWeatherByGPSCoord for lat={}, lon={}", lat, lon);

        WeatherDataResponse<WeatherDataModel> weatherDataModelWeatherDataResponse
                = new WeatherDataResponse<>(weatherDataModel);
        return Response.status(Response.Status.OK)
                .entity(weatherDataModelWeatherDataResponse)
                .build();
    }
}

