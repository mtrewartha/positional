package io.trewartha.positional.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.coordinates_degrees_decimal_fragment.*
import java.util.*

class CoordinatesDegreesDecimalFragment : CoordinatesFragment() {

    companion object {
        private const val FORMAT_LATITUDE = "% 10.5f"
        private const val FORMAT_LONGITUDE = "% 10.5f"
        private val LOCALE = Locale.getDefault()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.coordinates_degrees_decimal_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCoordinates(latitude, longitude)
    }

    override fun setCoordinates(latitude: Double, longitude: Double) {
        latitudeTextView?.text = String.format(LOCALE, FORMAT_LATITUDE, latitude)
        longitudeTextView?.text = String.format(LOCALE, FORMAT_LONGITUDE, longitude)
    }
}
