package io.trewartha.positional.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import io.trewartha.positional.R;

public class CoordinatesMGRSFragment extends CoordinatesFragment {

    private TextView coordinatesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.coordinates_mgrs_fragment, container, false);
        coordinatesTextView = view.findViewById(R.id.mgrs_text_view);
        setCoordinates(latitude, longitude);
        return view;
    }

    @Override
    public void setCoordinates(double latitude, double longitude) {
        if (coordinatesTextView != null) {
            final MGRSCoord mgrsCoord = MGRSCoord.fromLatLon(
                    Angle.fromDegreesLatitude(latitude),
                    Angle.fromDegreesLongitude(longitude)
            );
            coordinatesTextView.setText(mgrsCoord.toString());
        }
    }
}
