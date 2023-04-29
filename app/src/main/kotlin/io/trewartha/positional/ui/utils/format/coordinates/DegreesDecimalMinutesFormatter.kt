package io.trewartha.positional.ui.utils.format.coordinates

import android.content.Context
import android.location.Location.FORMAT_MINUTES
import android.location.Location.convert
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.ui.location.Coordinates
import java.util.Locale

class DegreesDecimalMinutesFormatter(
    private val context: Context,
    locale: Locale
) : CoordinatesFormatter {

    init {
        require(locale == Locale.getDefault()) {
            "This function can only format for the current default locale"
        }
    }

    override val format = CoordinatesFormat.DDM

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        listOf(
            coordinates?.latitude?.let { formatLatitude(it) },
            coordinates?.longitude?.let { formatLongitude(it) }
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.location_coordinates_copy_format_ddm,
            formatLatitude(coordinates.latitude),
            formatLongitude(coordinates.longitude)
        )

    private fun formatLatitude(latitude: Double): String {
        val ddmLat = replaceDelimiters(convert(latitude, FORMAT_MINUTES))
        return if (latitude >= 0.0) {
            "$ddmLat N"
        } else {
            "${ddmLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun formatLongitude(longitude: Double): String {
        val ddmLon = replaceDelimiters(convert(longitude, FORMAT_MINUTES))
        return if (longitude >= 0.0) {
            "$ddmLon E"
        } else {
            "${ddmLon.replaceFirst("-".toRegex(), "")} W"
        }
    }

    private fun replaceDelimiters(string: String): String =
        string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
}
