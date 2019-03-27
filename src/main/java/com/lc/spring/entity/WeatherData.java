package com.lc.spring.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name="weather_data")
public class WeatherData implements Serializable {

    @EmbeddedId
    private WeatherId weatherId;

    @Column(name="weather_description")
    private String weatherDescription;

    @Column(name="country")
    private String country;

    @Column(name="humidity")
    private long humidity;

    @Column(name="temp")
    private long temp;

    @Column(name="temp_min")
    private long tempMin;

    @Column(name="temp_max")
    private long tempMax;

    @Column(name="sun_rise")
    private long sunRise;

    @Column(name="sun_set")
    private long sunSet;

}
