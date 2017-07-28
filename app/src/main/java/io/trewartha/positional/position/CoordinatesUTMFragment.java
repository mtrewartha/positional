package io.trewartha.positional.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;
import io.trewartha.positional.R;

public class CoordinatesUTMFragment extends CoordinatesFragment {

    private static final String FORMAT = "%7.0f";
    private static final Locale LOCALE = Locale.getDefault();

    private TextView zoneTextView;
    private TextView eastingTextView;
    private TextView northingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.coordinates_utm_fragment, container, false);
        zoneTextView = view.findViewById(R.id.utm_zone_text_view);
        eastingTextView = view.findViewById(R.id.utm_easting_text_view);
        northingTextView = view.findViewById(R.id.utm_northing_text_view);
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
