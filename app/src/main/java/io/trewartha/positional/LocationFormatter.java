package io.trewartha.positional;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
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
    String getLatitude(@Nullable Location location, boolean decimalDegrees) {
        final Location nonNullLocation = location == null ? new Location("") : location;
        if (decimalDegrees) {
            return String.format(Locale.getDefault(), "%+03.5f", nonNullLocation.getLatitude());
        } else {
            return UnitConverter.getLatitudeAsDMS(nonNullLocation, 2);
        }
    }

    @NonNull
    String getLongitude(@Nullable Location location, boolean decimalDegrees) {
        final Location nonNullLocation = location == null ? new Location("") : location;
        if (decimalDegrees) {
            return String.format(Locale.getDefault(), "%+03.5f", nonNullLocation.getLongitude());
        } else {
            return UnitConverter.getLongitudeAsDMS(nonNullLocation, 2);
        }
    }

    @NonNull
    String getProviderStatus(int providerStatus) {
        switch (providerStatus) {
            case LocationProvider.OUT_OF_SERVICE:
                return context.getString(R.string.provider_status_out_of_service);
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                return context.getString(R.string.provider_status_temporarily_unavailable);
            default: // AVAILABLE
                return context.getString(R.string.provider_status_available);
        }
    }

    @NonNull
    String getSatellites(@Nullable Location location) {
        final int satellites;
        if (location == null) {
            satellites = 0;
        } else {
            final Bundle extras = location.getExtras();
            satellites = extras == null ? 0 : extras.getInt(LOCATION_EXTRAS_SATELLITES_KEY, -1);
        }
        return satellites < 0 ? context.getString(R.string.unknown) : String.format(LOCALE, FORMAT_SATELLITES, satellites);
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
