package io.trewartha.positional.ui.location.coordinates

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.trewartha.positional.R
import io.trewartha.positional.location.LocationFormatter
import kotlinx.android.synthetic.main.coordinates_utm_fragment.*

class CoordinatesUTMFragment : CoordinatesFragment() {

    private lateinit var locationFormatter: LocationFormatter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationFormatter = LocationFormatter(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.coordinates_utm_fragment, container, false)

    override fun setCoordinates(latitude: Double, longitude: Double) {
        coordinatesTextView.text = String.format(
                COORDINATES_FORMAT,
                locationFormatter.getUtmZone(latitude, longitude),
                locationFormatter.getUtmEasting(latitude, longitude),
                locationFormatter.getUtmNorthing(latitude, longitude)
        )
    }

    companion object {
        private const val COORDINATES_FORMAT = "%s\n%s\n%s"
    }
}
