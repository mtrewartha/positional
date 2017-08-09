package io.trewartha.positional.position

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.coordinates_degrees_minutes_seconds_fragment.*

class CoordinatesDegreesMinutesSecondsFragment : CoordinatesFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.coordinates_degrees_minutes_seconds_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCoordinates(latitude, longitude)
    }

    override fun setCoordinates(latitude: Double, longitude: Double) {
        latitudeTextView?.text = getLatitudeAsDMS(latitude)
        longitudeTextView?.text = getLongitudeAsDMS(longitude)
    }

    private fun getLatitudeAsDMS(latitude: Double): String {
        var latitudeDMS = Location.convert(latitude, Location.FORMAT_SECONDS)
        latitudeDMS = replaceDelimiters(latitudeDMS)
        if (latitude >= 0.0) {
            latitudeDMS += " N"
        } else {
            latitudeDMS = latitudeDMS.replaceFirst("-".toRegex(), "") + " S"
        }
        return latitudeDMS
    }

    private fun getLongitudeAsDMS(longitude: Double): String {
        var longitudeDMS = Location.convert(longitude, Location.FORMAT_SECONDS)
        longitudeDMS = replaceDelimiters(longitudeDMS)
        if (longitude >= 0.0) {
            longitudeDMS += " E"
        } else {
            longitudeDMS = longitudeDMS.replaceFirst("-".toRegex(), "") + " W"
        }
        return longitudeDMS
    }

    private fun replaceDelimiters(string: String): String {
        return string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
    }
}
