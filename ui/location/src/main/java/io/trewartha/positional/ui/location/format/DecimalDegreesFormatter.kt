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
            formatLatitudeOrLongitude(coordinates?.latitude),
            formatLatitudeOrLongitude(coordinates?.longitude),
        )

    override fun formatForCopy(coordinates: Coordinates): String =
        context.getString(
            R.string.ui_location_coordinates_copy_format_dd,
            formatLatitudeOrLongitude(coordinates.latitude),
            formatLatitudeOrLongitude(coordinates.longitude)
        )

    private fun formatLatitudeOrLongitude(latitudeOrLongitude: Double?) =
        latitudeOrLongitude?.let { FORMAT.format(locale, it) }
}

private const val FORMAT = "%3.5f°"
