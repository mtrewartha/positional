package io.trewartha.positional.position

import android.os.Bundle
import android.support.v4.app.Fragment

abstract class CoordinatesFragment : Fragment() {

    companion object {
        private const val STATE_LATITUDE = "latitude"
        private const val STATE_LONGITUDE = "longitude"
    }

    protected var latitude: Double = 0.0
    protected var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        latitude = savedInstanceState?.getDouble(STATE_LATITUDE) ?: 0.0
        longitude = savedInstanceState?.getDouble(STATE_LONGITUDE) ?: 0.0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(STATE_LATITUDE, latitude)
        outState.putDouble(STATE_LONGITUDE, longitude)
    }

    abstract fun setCoordinates(latitude: Double, longitude: Double)
}
