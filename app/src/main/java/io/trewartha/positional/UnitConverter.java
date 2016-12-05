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
        String latitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
        latitude = replaceDelimiters(latitude, decimalPlaces);
        latitude = latitude + " N";
        return latitude;
    }

    @NonNull
    static String getLongitudeAsDMS(@NonNull Location location, int decimalPlaces) {
        String longitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
        longitude = replaceDelimiters(longitude, decimalPlaces);
        longitude = longitude + " W";
        return longitude;
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
