package io.trewartha.positional.core.measurement

import earth.worldwind.geom.coords.UTMCoord
import kotlin.math.roundToInt

/**
 * Geographic coordinates in Universal Transverse Mercator (UTM) projection. See this
 * [Wikipedia article](https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system)
 * article for more information.
 *
 * @property zone UTM zone of the coordinates
 * @property hemisphere Hemisphere the coordinates are in
 * @property easting Easting within zone
 * @property northing Northing within zone
 */
public data class UtmCoordinates(
    val zone: Int,
    val hemisphere: Hemisphere,
    val easting: Distance,
    val northing: Distance
) : Coordinates {

    private val worldWindUtmCoordinates = try {
        val hemisphere = when (hemisphere) {
            Hemisphere.NORTH -> earth.worldwind.geom.coords.Hemisphere.N
            Hemisphere.SOUTH -> earth.worldwind.geom.coords.Hemisphere.S
        }
        require(easting.isFinite && !easting.isNegative) { "Invalid easting: $easting" }
        require(northing.isFinite && !northing.isNegative) { "Invalid northing: $northing" }
        val easting = easting.inMeters().magnitude
        val northing = northing.inMeters().magnitude
        UTMCoord.fromUTM(zone, hemisphere, easting, northing)
    } catch (exception: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid UTM coordinates: $this", exception)
    }

    override fun asGeodeticCoordinates(): GeodeticCoordinates =
        try {
            GeodeticCoordinates(
                worldWindUtmCoordinates.latitude.asAngle(),
                worldWindUtmCoordinates.longitude.asAngle()
            )
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid UTM coordinates")
        }

    override fun asMgrsCoordinates(): MgrsCoordinates = asGeodeticCoordinates().asMgrsCoordinates()

    override fun asUtmCoordinates(): UtmCoordinates? = this

    override fun toString(): String {
        val easting = easting.inRoundedAndPaddedMeters()
        val northing = northing.inRoundedAndPaddedMeters()
        return "$zone$hemisphere ${easting}m E ${northing}m N"
    }

    public companion object {

        /**
         * Number of digits to be used when printing eastings and northings
         */
        public const val EASTING_NORTHING_LENGTH: Int = 7

        /**
         * Character to be used to start/left pad printings of eastings and northings
         */
        public const val EASTING_NORTHING_PAD_CHAR: Char = '0'
    }
}

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString()
        .padStart(UtmCoordinates.EASTING_NORTHING_LENGTH, UtmCoordinates.EASTING_NORTHING_PAD_CHAR)
