package io.trewartha.positional.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.coordinates_utm_fragment.*
import java.util.*

class CoordinatesUTMFragment : CoordinatesFragment() {

    companion object {
        private const val FORMAT = "%7.0f"
        private val LOCALE = Locale.getDefault()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.coordinates_utm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCoordinates(latitude, longitude)
    }

    override fun setCoordinates(latitude: Double, longitude: Double) {
        val utmCoordinate = UTMCoord.fromLatLon(
                Angle.fromDegreesLatitude(latitude),
                Angle.fromDegreesLongitude(longitude)
        )
        val zone = utmCoordinate.zone.toString() + if ("gov.nasa.worldwind.avkey.North" == utmCoordinate.hemisphere) "N " else "S "
        val easting = String.format(LOCALE, FORMAT, utmCoordinate.easting) + "m E"
        val northing = String.format(LOCALE, FORMAT, utmCoordinate.northing) + "m N"

        zoneTextView?.text = zone
        eastingTextView?.text = easting
        northingTextView?.text = northing
    }
}
