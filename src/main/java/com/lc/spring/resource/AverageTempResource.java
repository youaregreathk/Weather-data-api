package com.lc.spring.resource;

import com.lc.spring.model.wrapper.GPSCoordinatesWrapper;
import com.lc.spring.model.response.WeatherDataResponse;
import com.lc.spring.service.WeatherService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class AverageTempResource {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WeatherService weatherService;

    @RequestMapping(value = "/average-temp/coordinates", method = RequestMethod.POST, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Security error"),
            @ApiResponse(code = 500, message = "Unexpected failure"),
            @ApiResponse(code = 503, message = "Service unavailable")})
    public Response getAverageByGPSCoord(@RequestBody GPSCoordinatesWrapper coordinateList) {

        logger.info("START:getAverageByGPSCoord");
        double averageTemp = weatherService.getAvgTempByCoordinates(coordinateList.getCoordinateList());
        logger.info("END:getAverageByGPSCoord");

        WeatherDataResponse<Double> weatherDataModelWeatherDataResponse = new WeatherDataResponse<>(averageTemp);
        return Response.status(Response.Status.OK)
                .entity(weatherDataModelWeatherDataResponse)
                .build();
    }
}
