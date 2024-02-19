package io.trewartha.positional.ui.location.format

import android.content.Context
import android.location.Location.FORMAT_SECONDS
import android.location.Location.convert
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R
import java.util.Locale

class DegreesMinutesSecondsFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override val format = CoordinatesFormat.DMS

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        listOf(
            coordinates?.latitude?.let { formatLatitude(it) },
            coordinates?.longitude?.let { formatLongitude(it) }
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.ui_location_coordinates_copy_format_dms,
            formatLatitude(coordinates.latitude),
            formatLongitude(coordinates.longitude)
        )

    private fun formatLatitude(latitude: Double): String {
        val components = convert(latitude, FORMAT_SECONDS).split(':')
        val degrees = components[0].toInt()
        val minutes = components[1].toInt()
        val seconds = components[2].normalizeDecimalSeparator().toFloat()
        return FORMAT.format(locale, degrees, minutes, seconds)
    }

    private fun formatLongitude(longitude: Double): String {
        val components = convert(longitude, FORMAT_SECONDS).split(':')
        val degrees = components[0].toInt()
        val minutes = components[1].toInt()
        val seconds = components[2].normalizeDecimalSeparator().toFloat()
        return FORMAT.format(locale, degrees, minutes, seconds)
    }
}

private const val FORMAT = "%1\$3d° %2\$2d' %3\$2.2f\""
