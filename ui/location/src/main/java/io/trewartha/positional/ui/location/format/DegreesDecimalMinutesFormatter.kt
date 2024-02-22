package io.trewartha.positional.ui.location.format

import android.content.Context
import android.location.Location.FORMAT_MINUTES
import android.location.Location.convert
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R
import java.util.Locale

class DegreesDecimalMinutesFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override val format = CoordinatesFormat.DDM

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        listOf(
            coordinates?.latitude?.let { formatForDisplay(it) },
            coordinates?.longitude?.let { formatForDisplay(it) }
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.ui_location_coordinates_copy_format_ddm,
            formatForCopy(coordinates.latitude),
            formatForCopy(coordinates.longitude)
        )

    private fun formatForCopy(value: Double): String {
        val components = convert(value, FORMAT_MINUTES).split(':')
        val degrees = components[0].toInt()
        val minutes = components[1].normalizeDecimalSeparator().toFloat()
        return FORMAT_COPY.format(locale, degrees, minutes)
    }

    private fun formatForDisplay(value: Double): String {
        val components = convert(value, FORMAT_MINUTES).split(':')
        val degrees = components[0].toInt()
        val minutes = components[1].normalizeDecimalSeparator().toFloat()
        return FORMAT_DISPLAY.format(locale, degrees, minutes)
    }
}

private const val FORMAT_COPY = "%1d° %2$.3f'"
private const val FORMAT_DISPLAY = "%1\$4d° %2\$06.3f'"
