package io.trewartha.positional;

import android.location.Location;
import android.support.annotation.NonNull;

class UnitConverter {

    private static final double FEET_PER_METER = 3.2808399200439453;
    private static final double MPH_PER_MPS = 0.44704;

    static double metersToFeet(double meters) {
        return meters * FEET_PER_METER;
    }

    static double metersPerSecondToMilesPerHour(double metersPerSecond) {
        return metersPerSecond * MPH_PER_MPS;
    }

    @NonNull
    static String getLatitudeAsDMS(@NonNull Location location, int decimalPlaces) {
        final double latitude = location.getLatitude();
        String latitudeDMS = Location.convert(latitude, Location.FORMAT_SECONDS);
        latitudeDMS = replaceDelimiters(latitudeDMS, decimalPlaces);
        if (latitude >= 0.0) {
            latitudeDMS = latitudeDMS + " N";
        } else {
            latitudeDMS = latitudeDMS.replaceFirst("-", "") + " S";
        }
        return latitudeDMS;
    }

    @NonNull
    static String getLongitudeAsDMS(@NonNull Location location, int decimalPlaces) {
        final double longitude = location.getLongitude();
        String longitudeDMS = Location.convert(longitude, Location.FORMAT_SECONDS);
        longitudeDMS = replaceDelimiters(longitudeDMS, decimalPlaces);
        if (longitude >= 0.0) {
            longitudeDMS = longitudeDMS + " E";
        } else {
            longitudeDMS = longitudeDMS.replaceFirst("-", "") + " W";
        }
        return longitudeDMS;
    }

    @NonNull
    private static String replaceDelimiters(String string, int decimalPlace) {
        string = string.replaceFirst(":", "° ");
        string = string.replaceFirst(":", "' ");
        int pointIndex = string.indexOf(".");
        int endIndex = pointIndex + 1 + decimalPlace;
        if (endIndex < string.length()) {
            string = string.substring(0, endIndex);
        }
        string = string + "\"";
        return string;
    }
}
