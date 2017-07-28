package io.trewartha.positional.position;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.trewartha.positional.R;

public class CoordinatesDegreesMinutesSecondsFragment extends CoordinatesFragment {

    private TextView latitudeTextView;
    private TextView longitudeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.coordinates_degrees_minutes_seconds_fragment, container, false);
        latitudeTextView = view.findViewById(R.id.degrees_minutes_seconds_latitude_text_view);
        longitudeTextView = view.findViewById(R.id.degrees_minutes_seconds_longitude_text_view);
        setCoordinates(latitude, longitude);
        return view;
    }

    @Override
    public void setCoordinates(double latitude, double longitude) {
        if (latitudeTextView != null && longitudeTextView != null) {
            latitudeTextView.setText(getLatitudeAsDMS(latitude));
            longitudeTextView.setText(getLongitudeAsDMS(longitude));
        }
    }

    @NonNull
    private String getLatitudeAsDMS(double latitude) {
        String latitudeDMS = Location.convert(latitude, Location.FORMAT_SECONDS);
        latitudeDMS = replaceDelimiters(latitudeDMS);
        if (latitude >= 0.0) {
            latitudeDMS = latitudeDMS + " N";
        } else {
            latitudeDMS = latitudeDMS.replaceFirst("-", "") + " S";
        }
        return latitudeDMS;
    }

    @NonNull
    private String getLongitudeAsDMS(double longitude) {
        String longitudeDMS = Location.convert(longitude, Location.FORMAT_SECONDS);
        longitudeDMS = replaceDelimiters(longitudeDMS);
        if (longitude >= 0.0) {
            longitudeDMS = longitudeDMS + " E";
        } else {
            longitudeDMS = longitudeDMS.replaceFirst("-", "") + " W";
        }
        return longitudeDMS;
    }

    @NonNull
    private String replaceDelimiters(@NonNull String string) {
        return string.replaceFirst(":", "° ").replaceFirst(":", "' ") + "\"";
    }
}
