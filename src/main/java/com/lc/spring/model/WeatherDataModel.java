package com.lc.spring.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@ApiModel(value = "ReferralModel", description = "Model class for referring a friend")
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeatherDataModel {

    @ApiModelProperty(dataType = "string", required = true, value = "WeatherDescription")
    @NotNull(message = "Value is required")
    private String weatherDescription;

    @ApiModelProperty(dataType = "double", required = true, value = "Latitude")
    @NotNull(message = "Value is required")
    private double latitude;

    @ApiModelProperty(dataType = "double", required = true, value = "Longitude")
    @NotNull(message = "Value is required")
    private double longitude;

    @ApiModelProperty(dataType = "string", required = true, value = "Country")
    @NotNull(message = "Value is required")
    private String country;

    @ApiModelProperty(dataType = "long", required = true, value = "Humidity")
    @NotNull(message = "Value is required")
    private long humidity;

    @ApiModelProperty(dataType = "long", required = true, value = "Temp")
    @NotNull(message = "Value is required")
    private long temp;

    @ApiModelProperty(dataType = "long", required = true, value = "TempMin")
    @NotNull(message = "Value is required")
    private long tempMin;

    @ApiModelProperty(dataType = "long", required = true, value = "TempMax")
    @NotNull(message = "Value is required")
    private long tempMax;

    @ApiModelProperty(dataType = "long", required = true, value = "SunRise")
    @NotNull(message = "Value is required")
    private long sunRise;

    @ApiModelProperty(dataType = "long", required = true, value = "SunSet")
    @NotNull(message = "Value is required")
    private long sunSet;

    @ApiModelProperty(dataType = "string", required = true, value = "TimeStamp")
    @NotNull(message = "Timestamp is required")
    private String timeStamp;
}
