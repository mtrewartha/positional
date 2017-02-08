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
    private static final String FORMAT_SPEED = "%.0f";
    private static final Locale LOCALE = Locale.getDefault();

    @NonNull private Context context;

    LocationFormatter(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    String getAccuracy(@Nullable Location location, boolean metric) {
        final int accuracy;
        if (location == null) {
            accuracy = 0;
        } else if (metric) {
            accuracy = (int) location.getAccuracy();
        } else {
            accuracy = (int) UnitConverter.metersToFeet(location.getAccuracy());
        }
        return String.format(LOCALE, FORMAT_ACCURACY, accuracy);
    }

    @NonNull
    String getBearing(@Nullable Location location) {
        final int bearing = location == null ? 0 : (int) location.getBearing();
        return String.format(LOCALE, FORMAT_BEARING, bearing);
    }

    @NonNull
    String getElevation(@Nullable Location location, boolean metric) {
        final int elevation;
        if (location == null) {
            elevation = 0;
        } else if (metric) {
            elevation = (int) location.getAltitude();
        } else {
            elevation = (int) UnitConverter.metersToFeet((float) location.getAltitude());
        }
        return String.format(LOCALE, FORMAT_ELEVATION, elevation);
    }

    @NonNull
    String getSpeed(@Nullable Location location, boolean metric) {
        final float speed;
        if (location == null) {
            speed = 0.0f;
        } else if (metric) {
            speed = UnitConverter.metersPerSecondToKilometersPerHour(location.getSpeed());
        } else {
            speed = UnitConverter.metersPerSecondToMilesPerHour(location.getSpeed());
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
        int stringRes = metric ? R.string.unit_kmh : R.string.unit_mph;
        return context.getString(stringRes);
    }
}
