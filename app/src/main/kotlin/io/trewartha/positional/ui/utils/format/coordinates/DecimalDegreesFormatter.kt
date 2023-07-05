package io.trewartha.positional.ui.utils.format.coordinates

import android.content.Context
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.ui.location.Coordinates
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
            R.string.location_coordinates_copy_format_dd,
            formatLatitudeOrLongitude(coordinates.latitude),
            formatLatitudeOrLongitude(coordinates.longitude)
        )

    private fun formatLatitudeOrLongitude(latitudeOrLongitude: Double?) =
        latitudeOrLongitude?.let { FORMAT.format(locale, it) }
}

private const val FORMAT = "%3.5f°"
