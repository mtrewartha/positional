package io.trewartha.positional.ui.location

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.trewartha.positional.R
import io.trewartha.positional.location.LocationFormatter
import kotlinx.android.synthetic.main.coordinates_degrees_decimal_fragment.*

class CoordinatesDegreesDecimalFragment : CoordinatesFragment() {

    private lateinit var locationFormatter: LocationFormatter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationFormatter = LocationFormatter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.coordinates_degrees_decimal_fragment, container, false)

    override fun setCoordinates(latitude: Double, longitude: Double) {
        latitudeTextView.text = locationFormatter.getDecimalLatitude(latitude)
        longitudeTextView.text = locationFormatter.getDecimalLongitude(longitude)
    }
}
