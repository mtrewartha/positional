package io.trewartha.positional;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DegreesMinutesSecondsFragment extends CoordinatesFragment {

    @BindView(R.id.degrees_minutes_seconds_latitude_text_view) @Nullable TextView latitudeTextView;
    @BindView(R.id.degrees_minutes_seconds_longitude_text_view) @Nullable TextView longitudeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.degrees_minutes_seconds_fragment, container, false);
        ButterKnife.bind(this, view);
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
