package io.trewartha.positional.coordinates;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.trewartha.positional.R;

public class DegreesDecimalFragment extends CoordinatesFragment {

    private static final String FORMAT_LATITUDE = "%+10.5f";
    private static final String FORMAT_LONGITUDE = "%+10.5f";
    private static final Locale LOCALE = Locale.getDefault();

    @BindView(R.id.degrees_decimal_latitude_text_view) @Nullable TextView latitudeTextView;
    @BindView(R.id.degrees_decimal_longitude_text_view) @Nullable TextView longitudeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.degrees_decimal_fragment, container, false);
        ButterKnife.bind(this, view);
        setCoordinates(latitude, longitude);
        return view;
    }

    @Override
    public void setCoordinates(double latitude, double longitude) {
        if (latitudeTextView != null && longitudeTextView != null) {
            latitudeTextView.setText(String.format(LOCALE, FORMAT_LATITUDE, latitude));
            longitudeTextView.setText(String.format(LOCALE, FORMAT_LONGITUDE, longitude));
        }
    }
}
