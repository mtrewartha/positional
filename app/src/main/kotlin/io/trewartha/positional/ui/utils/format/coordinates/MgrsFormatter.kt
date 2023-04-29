package io.trewartha.positional.ui.utils.format.coordinates

import android.content.Context
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.ui.location.Coordinates

class MgrsFormatter(private val context: Context) : CoordinatesFormatter {

    override val format = CoordinatesFormat.MGRS

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        coordinates?.toMgrsCoordinates()?.toString()?.split(' ')
            ?: List(MGRS_COORDINATES_SIZE) { null }

    override fun formatForCopy(coordinates: Coordinates): String {
        val formattedLines = formatForDisplay(coordinates).map { it.orEmpty() }
        return context.getString(
            R.string.location_coordinates_copy_format_mgrs,
            formattedLines[0],
            formattedLines[1],
            formattedLines[2],
        )
    }

    private fun Coordinates.toMgrsCoordinates(): MGRSCoord =
        MGRSCoord.fromLatLon(
            Angle.fromDegreesLatitude(latitude),
            Angle.fromDegreesLongitude(longitude)
        )

    private companion object {
        private const val MGRS_COORDINATES_SIZE = 3
    }
}
