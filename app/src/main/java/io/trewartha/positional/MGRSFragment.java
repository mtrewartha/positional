package io.trewartha.positional;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;

public class MGRSFragment extends CoordinatesFragment {

    @BindView(R.id.mgrs_text_view) @Nullable TextView coordinatesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.mgrs_fragment, container, false);
        ButterKnife.bind(this, view);
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
