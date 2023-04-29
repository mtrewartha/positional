package io.trewartha.positional.ui.utils.format.coordinates

import android.content.Context
import android.location.Location.FORMAT_SECONDS
import android.location.Location.convert
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.ui.location.Coordinates
import java.util.Locale

class DegreesMinutesSecondsFormatter(
    private val context: Context,
    locale: Locale
) : CoordinatesFormatter {

    init {
        require(locale == Locale.getDefault()) {
            "This function can only format for the current default locale"
        }
    }

    override val format = CoordinatesFormat.DMS

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        listOf(
            coordinates?.latitude?.let { formatLatitude(it) },
            coordinates?.longitude?.let { formatLongitude(it) }
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.location_coordinates_copy_format_dms,
            formatLatitude(coordinates.latitude),
            formatLongitude(coordinates.longitude)
        )

    private fun formatLatitude(latitude: Double): String {
        val dmsLat = replaceDelimiters(convert(latitude, FORMAT_SECONDS))
        return if (latitude >= 0.0) {
            "$dmsLat N"
        } else {
            "${dmsLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun formatLongitude(longitude: Double): String {
        val dmsLon = replaceDelimiters(convert(longitude, FORMAT_SECONDS))
        return if (longitude >= 0.0) {
            "$dmsLon E"
        } else {
            "${dmsLon.replaceFirst("-".toRegex(), "")} W"
        }
    }

    private fun replaceDelimiters(string: String): String =
        string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
}
