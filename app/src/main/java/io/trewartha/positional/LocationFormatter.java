package io.trewartha.positional;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

class LocationFormatter {

    private static final String FORMAT_ACCURACY = "%d";
    private static final String FORMAT_BEARING = "%d";
    private static final String FORMAT_ELEVATION = "%,d";
    private static final String FORMAT_SATELLITES = "%d";
    private static final String FORMAT_SPEED = "%,d";
    private static final Locale LOCALE = Locale.getDefault();
    private static final String LOCATION_EXTRAS_SATELLITES_KEY = "satellites";

    @NonNull private Context context;

    LocationFormatter(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    String getAccuracy(@Nullable Location location, boolean metric) {
        int accuracy = location == null || !location.hasAccuracy() ? 0 : (int) location.getAccuracy();
        if (!metric) {
            accuracy = (int) UnitConverter.metersToFeet(accuracy);
        }
        return String.format(LOCALE, FORMAT_ACCURACY, accuracy);
    }

    @NonNull
    String getBearing(@Nullable Location location) {
        final int bearing = location == null || !location.hasBearing() ? 0 : (int) location.getBearing();
        return String.format(LOCALE, FORMAT_BEARING, bearing);
    }

    @NonNull
    String getElevation(@Nullable Location location, boolean metric) {
        int elevation = location == null || !location.hasAltitude() ? 0 : (int) location.getAltitude();
        if (!metric) {
            elevation = (int) UnitConverter.metersToFeet(elevation);
        }
        return String.format(LOCALE, FORMAT_ELEVATION, elevation);
    }

    @NonNull
    String getSpeed(@Nullable Location location, boolean metric) {
        int speed = location == null || !location.hasSpeed() ? 0 : (int) location.getSpeed();
        if (!metric) {
            speed = (int) UnitConverter.metersPerSecondToMilesPerHour(speed);
        }
        return String.format(LOCALE, FORMAT_SPEED, speed);
    }

    @NonNull
    String getDistanceUnit(boolean metric) {
        int stringRes = metric ? R.string.unit_meters : R.string.unit_feet;
        return context.getString(stringRes);
    }

    @NonNull
    String getSpeedUnit(boolean metric) {
        int stringRes = metric ? R.string.unit_mps : R.string.unit_mph;
        return context.getString(stringRes);
    }
}
