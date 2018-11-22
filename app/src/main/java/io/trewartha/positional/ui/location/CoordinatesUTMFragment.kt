package io.trewartha.positional.ui.location

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
        zoneTextView?.text = locationFormatter.getUtmZone(latitude, longitude)
        eastingTextView?.text = locationFormatter.getUtmEasting(latitude, longitude)
        northingTextView?.text = locationFormatter.getUtmNorthing(latitude, longitude)
    }
}
