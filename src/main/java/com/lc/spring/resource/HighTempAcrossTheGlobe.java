package com.lc.spring.resource;

import com.lc.spring.model.response.WeatherDataResponse;
import com.lc.spring.service.WeatherService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HighTempAcrossTheGlobe {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WeatherService weatherService;

    @RequestMapping(value = "/weather/time/{timeStamp}", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Security error"),
            @ApiResponse(code = 500, message = "Unexpected failure"),
            @ApiResponse(code = 503, message = "Service unavailable")})
    public Response getHighTempAcrossTheGlobe(@PathVariable(value = "timeStamp", required = true) String timeStamp) {

        logger.info("START:getAverageByGPSCoord");
        List<Long> highTempList = weatherService.getHighTempByTime(timeStamp);
        logger.info("END:getAverageByGPSCoord");

        WeatherDataResponse<List<Long>> weatherDataModelWeatherDataResponse = new WeatherDataResponse<>(highTempList);
        return Response.status(Response.Status.OK)
                .entity(weatherDataModelWeatherDataResponse)
                .build();
    }
}
