package com.lc.spring.service;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {

    public static String convertCoordToGeoCoord(double lat, double lon) {
        String result = "";
        try {
            H3Core h3 = H3Core.newInstance();
            int res = 9;
            result = h3.geoToH3Address(lat, lon, res);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    public static List<GeoCoord> convertGeoCoorToCoord(String geoCoordinate) {
        List<GeoCoord> result = new ArrayList<>();
        try {
            H3Core h3 = H3Core.newInstance();
            result = h3.h3ToGeoBoundary(geoCoordinate);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    public static String getCurTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        return simpleDateFormat.format(new Date());
    }

    public static double getAvergeTempInFahrenheit(long culTempSum, int totalSize) {
        return (9/5 * ((culTempSum/ totalSize) - 273.15) + 32) ;
    }

}
