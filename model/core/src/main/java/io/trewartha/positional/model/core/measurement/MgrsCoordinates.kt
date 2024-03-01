package io.trewartha.positional.model.core.measurement

import earth.worldwind.geom.coords.MGRSCoord
import kotlin.math.roundToInt

/**
 * Geographic coordinates in Military Grid Reference System (MGRS) format. See this
 * [Wikipedia article](https://en.wikipedia.org/wiki/Military_Grid_Reference_System) for more
 * information.
 *
 * @property gridZoneDesignator If the coordinates fall within latitudes of -80° and 84°, then this
 * should be the UTM zone and MGRS latitude band. If the coordinates fall outside those latitudes,
 * then this should be one of A, B, Y, or Z. See the link above for more information.
 * @property gridSquareID 100 km grid square ID
 * @property easting Easting within 100 km grid square
 * @property northing Northing within 100 km grid square
 */
data class MgrsCoordinates(
    val gridZoneDesignator: String,
    val gridSquareID: String,
    val easting: Distance,
    val northing: Distance
) : Coordinates {

    override fun asGeodeticCoordinates(): GeodeticCoordinates =
        with(MGRSCoord.fromString(toString())) {
            GeodeticCoordinates(latitude.asAngle(), longitude.asAngle())
        }

    override fun asMgrsCoordinates(): MgrsCoordinates = this

    override fun asUtmCoordinates(): UtmCoordinates? = asGeodeticCoordinates().asUtmCoordinates()

    override fun toString(): String {
        val easting = this.easting.format()
        val northing = this.northing.format()
        return "$gridZoneDesignator $gridSquareID $easting $northing"
    }

    private fun Distance.format() =
        this.inMeters().magnitude.roundToInt().toString().padStart(NUMERICAL_LOCATION_FORMAT, ZERO)
}

private const val NUMERICAL_LOCATION_FORMAT = 5
private const val ZERO = '0'
