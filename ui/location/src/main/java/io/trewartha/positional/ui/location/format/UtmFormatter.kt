package io.trewartha.positional.ui.location.format

import android.content.Context
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.core.measurement.Hemisphere
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R
import java.util.Locale

/**
 * Formats coordinates in the Universal Transverse Mercator (UTM) system
 */
class UtmFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override val format = CoordinatesFormat.UTM

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        coordinates?.let {
            it.asUtmCoordinates()?.let { utmCoordinates ->
                val hemisphereAbbreviation = context.getString(
                    when (utmCoordinates.hemisphere) {
                        Hemisphere.NORTH -> R.string.ui_location_coordinates_utm_hemisphere_north
                        Hemisphere.SOUTH -> R.string.ui_location_coordinates_utm_hemisphere_south
                    }
                )
                val zoneAndHemisphere = "${utmCoordinates.zone}$hemisphereAbbreviation"
                val easting =
                    EASTING_FORMAT.format(locale, utmCoordinates.easting.inMeters().magnitude)
                val northing =
                    NORTHING_FORMAT.format(locale, utmCoordinates.northing.inMeters().magnitude)
                listOf(zoneAndHemisphere, easting, northing)
            } ?: List(UTM_COORDINATES_SIZE) { "" }
        } ?: List(UTM_COORDINATES_SIZE) { null }

    override fun formatForCopy(coordinates: Coordinates): String =
        formatForDisplay(coordinates).joinToString(" ")

}

private const val NUMBER_FORMAT = "%1$.0f"
private const val EASTING_FORMAT = "${NUMBER_FORMAT}m E"
private const val NORTHING_FORMAT = "${NUMBER_FORMAT}m N"
private const val UTM_COORDINATES_SIZE = 3
