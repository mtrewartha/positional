package io.trewartha.positional.ui.location.format

import android.content.Context
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R
import java.util.Locale

class DecimalDegreesFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override val format = CoordinatesFormat.DD

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        listOf(
            coordinates?.latitude?.let { FORMAT_DISPLAY.format(locale, it) },
            coordinates?.longitude?.let { FORMAT_DISPLAY.format(locale, it) }
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.ui_location_coordinates_copy_format_dd,
            FORMAT_COPY.format(locale, coordinates.latitude),
            FORMAT_COPY.format(locale, coordinates.longitude)
        )
}

private const val FORMAT_COPY = "%.5f°"
private const val FORMAT_DISPLAY = "%10.5f°"
