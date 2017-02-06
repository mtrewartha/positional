package io.trewartha.positional;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class UTMFragment extends CoordinatesFragment {

    private static final String FORMAT = "%7.0f";
    private static final Locale LOCALE = Locale.getDefault();

    @BindView(R.id.utm_zone_text_view) @Nullable TextView zoneTextView;
    @BindView(R.id.utm_easting_text_view) @Nullable TextView eastingTextView;
    @BindView(R.id.utm_northing_text_view) @Nullable TextView northingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.utm_fragment, container, false);
        ButterKnife.bind(this, view);
        setCoordinates(latitude, longitude);
        return view;
    }

    @Override
    public void setCoordinates(double latitude, double longitude) {
        if (zoneTextView != null && eastingTextView != null && northingTextView != null) {
            final UTMCoord utmCoord = UTMCoord.fromLatLon(
                    Angle.fromDegreesLatitude(latitude),
                    Angle.fromDegreesLongitude(longitude)
            );
            final String zone = utmCoord.getZone() + ("gov.nasa.worldwind.avkey.North".equals(utmCoord.getHemisphere()) ? "N " : "S ");
            final String easting = String.format(LOCALE, FORMAT, utmCoord.getEasting()) + "m E";
            final String northing = String.format(LOCALE, FORMAT, utmCoord.getNorthing()) + "m N";

            zoneTextView.setText(zone);
            eastingTextView.setText(easting);
            northingTextView.setText(northing);
        }
    }
}
