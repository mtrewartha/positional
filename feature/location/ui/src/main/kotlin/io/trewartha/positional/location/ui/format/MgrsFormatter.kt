package io.trewartha.positional.location.ui.format

import io.trewartha.positional.core.measurement.Coordinates
import io.trewartha.positional.core.measurement.Distance
import io.trewartha.positional.core.measurement.MgrsCoordinates.Companion.EASTING_NORTHING_LENGTH
import io.trewartha.positional.core.measurement.MgrsCoordinates.Companion.EASTING_NORTHING_PAD_CHAR
import kotlin.math.roundToInt

/**
 * Formats coordinates in the Military Grid Reference System (MGRS)
 */
public class MgrsFormatter : CoordinatesFormatter {

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        coordinates?.asMgrsCoordinates()?.let { mgrsCoordinates ->
            with(mgrsCoordinates) {
                listOf(
                    "${gridZoneDesignator} $gridSquareID",
                    EASTING_NORTHING_FORMAT.format(easting.inRoundedMeters()),
                    EASTING_NORTHING_FORMAT.format(northing.inRoundedMeters()),
                )
            }
        } ?: List(FORMAT_DISPLAY_LINE_COUNT) { null }

    override fun formatForCopy(coordinates: Coordinates): String =
        coordinates.asMgrsCoordinates().toString()
}

private fun Distance.inRoundedMeters(): Int = inMeters().magnitude.roundToInt()

private const val EASTING_NORTHING_FORMAT =
    "%${EASTING_NORTHING_PAD_CHAR}${EASTING_NORTHING_LENGTH}d"
private const val FORMAT_DISPLAY_LINE_COUNT = 3
