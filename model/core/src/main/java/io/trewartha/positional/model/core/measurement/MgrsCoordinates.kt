package io.trewartha.positional.model.core.measurement

import earth.worldwind.geom.coords.MGRSCoord
import kotlin.math.roundToInt

/**
 * Geographic coordinates in Military Grid Reference System (MGRS) format. See this
 * [Wikipedia article](https://en.wikipedia.org/wiki/Military_Grid_Reference_System) for more
 * information.
 *
 * @property zone MGRS/UTM zone of the coordinates
 * @property band Latitude band of the coordinates
 * @property hundredKMSquareID 100km grid square ID
 * @property easting Easting within 100km grid square
 * @property northing Northing within 100km grid square
 */
data class MgrsCoordinates(
    val zone: Int,
    val band: String,
    val hundredKMSquareID: String,
    val easting: Distance,
    val northing: Distance
) : Coordinates {

    override fun asGeodeticCoordinates(): GeodeticCoordinates =
        with(MGRSCoord.fromString(toString())) {
            GeodeticCoordinates(latitude.asAngle(), longitude.asAngle())
        }

    override fun asMgrsCoordinates(): MgrsCoordinates = this

    override fun asUtmCoordinates(): UtmCoordinates = asGeodeticCoordinates().asUtmCoordinates()

    override fun toString(): String {
        val easting = easting.format()
        val northing = northing.format()
        return "$zone$band $hundredKMSquareID $easting $northing"
    }

    private fun Distance.format() =
        this.inMeters().magnitude.roundToInt().toString().padStart(NUMERICAL_LOCATION_FORMAT, ZERO)

    private companion object {
        private const val NUMERICAL_LOCATION_FORMAT = 5
        private const val ZERO = '0'
    }
}
