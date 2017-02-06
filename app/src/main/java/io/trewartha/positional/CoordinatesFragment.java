package io.trewartha.positional;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class CoordinatesFragment extends Fragment {

    protected final String STATE_LATITUDE = "latitude";
    protected final String STATE_LONGITUDE = "longitude";

    protected double latitude;
    protected double longitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            latitude = savedInstanceState.getDouble(STATE_LATITUDE);
            longitude = savedInstanceState.getDouble(STATE_LONGITUDE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(STATE_LATITUDE, latitude);
        outState.putDouble(STATE_LONGITUDE, longitude);
    }

    public abstract void setCoordinates(double latitude, double longitude);
}
