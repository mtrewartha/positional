package io.trewartha.positional.ui.location.format

import android.content.Context
import earth.worldwind.geom.Angle.Companion.degrees
import earth.worldwind.geom.coords.MGRSCoord
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R

class MgrsFormatter(private val context: Context) : CoordinatesFormatter {

    override val format = CoordinatesFormat.MGRS

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        coordinates?.toMgrsCoordinates()?.toString()?.split(' ')
            ?: List(MGRS_COORDINATES_SIZE) { null }

    override fun formatForCopy(coordinates: Coordinates): String {
        val formattedLines = formatForDisplay(coordinates).map { it.orEmpty() }
        return context.getString(
            R.string.ui_location_coordinates_copy_format_mgrs,
            formattedLines[0],
            formattedLines[1],
            formattedLines[2],
        )
    }

    private fun Coordinates.toMgrsCoordinates(): MGRSCoord =
        MGRSCoord.fromLatLon(latitude.degrees, longitude.degrees)

    private companion object {
        private const val MGRS_COORDINATES_SIZE = 3
    }
}
