package io.trewartha.positional.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.trewartha.positional.R;

public class CoordinatesDegreesDecimalFragment extends CoordinatesFragment {

    private static final String FORMAT_LATITUDE = "% 10.5f";
    private static final String FORMAT_LONGITUDE = "% 10.5f";
    private static final Locale LOCALE = Locale.getDefault();

    @BindView(R.id.degrees_decimal_latitude_text_view) TextView latitudeTextView;
    @BindView(R.id.degrees_decimal_longitude_text_view) TextView longitudeTextView;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.coordinates_degrees_decimal_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        setCoordinates(latitude, longitude);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setCoordinates(double latitude, double longitude) {
        if (latitudeTextView != null && longitudeTextView != null) {
            String latitudeText = String.format(LOCALE, FORMAT_LATITUDE, latitude);
            String longitudeText = String.format(LOCALE, FORMAT_LONGITUDE, longitude);
            latitudeTextView.setText(latitudeText);
            longitudeTextView.setText(longitudeText);
        }
    }
}
