package io.trewartha.positional.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.coordinates_mgrs_fragment.*

class CoordinatesMGRSFragment : CoordinatesFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.coordinates_mgrs_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCoordinates(latitude, longitude)
    }

    override fun setCoordinates(latitude: Double, longitude: Double) {
        val mgrsCoordinate = MGRSCoord.fromLatLon(
                Angle.fromDegreesLatitude(latitude),
                Angle.fromDegreesLongitude(longitude)
        )
        coordinatesTextView?.text = mgrsCoordinate.toString()
    }
}
