package io.trewartha.positional.location.ui.format

import android.content.Context
import android.location.Location.FORMAT_SECONDS
import android.location.Location.convert
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.Coordinates
import io.trewartha.positional.location.ui.R
import java.util.Locale

/**
 * Formats coordinates as degrees, minutes, and seconds
 */
class DegreesMinutesSecondsFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> {
        val geodeticCoordinates = coordinates?.asGeodeticCoordinates()
        return listOf(
            geodeticCoordinates?.latitude?.let { FORMAT_DISPLAY.format(it) },
            geodeticCoordinates?.longitude?.let { FORMAT_DISPLAY.format(it) }
        )
    }

    override fun formatForCopy(coordinates: Coordinates): String {
        val geodeticCoordinates = coordinates.asGeodeticCoordinates()
        return context.getString(
            R.string.ui_location_coordinates_copy_format_dms,
            FORMAT_COPY.format(geodeticCoordinates.latitude),
            FORMAT_COPY.format(geodeticCoordinates.longitude)
        )
    }

    private fun String.format(angle: Angle): String {
        val components = convert(angle.inDegrees().magnitude, FORMAT_SECONDS).split(':')
        val degrees = components[0].toInt()
        val minutes = components[1].toInt()
        val seconds = components[2].normalizeDecimalSeparator().toFloat()
        return format(locale, degrees, minutes, seconds)
    }
}

private const val FORMAT_COPY = "%1\$d° %2\$d' %3\$.2f\""
private const val FORMAT_DISPLAY = "%1\$4d° %2\$2d' %3\$2.2f\""
