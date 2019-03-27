package com.lc.spring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class WeatherId implements Serializable {

    @Column(name = "geo_coordinate")
    private String geoCoordinate;

    @Column(name = "time_stamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timeStamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherId)) return false;
        WeatherId that = (WeatherId) o;
        return Objects.equals(getGeoCoordinate(), that.getGeoCoordinate()) &&
                Objects.equals(getTimeStamp(), that.getTimeStamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeoCoordinate(), getTimeStamp());
    }

}