package com.lc.spring.service;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public static List<String> convertCoordListToGeoList(List<List<Double>> coordinates) {
        List<String> result = coordinates.stream()
                .map(t -> convertCoordToGeoCoord(t.get(0), t.get(1)))
                .collect(Collectors.toList());
        return result;
    }
}
