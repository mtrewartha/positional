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

    private val worldWindMgrsCoordinates = try {
        require(easting.isFinite && !easting.isNegative) { "Invalid easting: $easting" }
        require(northing.isFinite && !northing.isNegative) { "Invalid northing: $northing" }
        val easting = easting.inRoundedAndPaddedMeters()
        val northing = northing.inRoundedAndPaddedMeters()
        MGRSCoord.fromString("$gridZoneDesignator $gridSquareID $easting $northing")
    } catch (exception: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid MGRS coordinates: $this", exception)
    }

    override fun asGeodeticCoordinates(): GeodeticCoordinates =
        with(worldWindMgrsCoordinates) {
            GeodeticCoordinates(latitude.asAngle(), longitude.asAngle())
        }

    override fun asMgrsCoordinates(): MgrsCoordinates = this

    override fun asUtmCoordinates(): UtmCoordinates? = asGeodeticCoordinates().asUtmCoordinates()

    override fun toString(): String {
        val easting = easting.inRoundedAndPaddedMeters()
        val northing = northing.inRoundedAndPaddedMeters()
        return "$gridZoneDesignator$gridSquareID$easting$northing"
    }

    companion object {

        /**
         * Number of digits to be used when printing eastings and northings
         */
        const val EASTING_NORTHING_LENGTH = 5

        /**
         * Character to be used to start/left pad printings of eastings and northings
         */
        const val EASTING_NORTHING_PAD_CHAR = '0'
    }
}

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString().padStart(
        MgrsCoordinates.EASTING_NORTHING_LENGTH,
        MgrsCoordinates.EASTING_NORTHING_PAD_CHAR
    )
