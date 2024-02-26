package io.trewartha.positional.model.core.measurement

import earth.worldwind.geom.Angle.Companion.degrees
import earth.worldwind.geom.coords.MGRSCoord
import earth.worldwind.geom.coords.UTMCoord
import kotlin.math.roundToInt

/**
 * Geographic coordinates abstraction
 */
sealed interface Coordinates {

    /**
     * Convert the coordinates to geodetic coordinates
     */
    fun asGeodeticCoordinates(): GeodeticCoordinates

    /**
     * Convert the coordinates to MGRS coordinates
     */
    fun asMgrsCoordinates(): MgrsCoordinates

    /**
     * Convert the coordinates to UTM coordinates
     */
    fun asUtmCoordinates(): UtmCoordinates
}

/**
 * Geodetic coordinates with latitude and longitude both given in decimal degrees. See this
 * [Wikipedia article](https://en.wikipedia.org/wiki/Geodetic_coordinates) for more information.
 *
 * @property latitude Angle of latitude
 * @property longitude Angle of longitude
 */
data class GeodeticCoordinates(val latitude: Angle, val longitude: Angle) : Coordinates {

    override fun asGeodeticCoordinates(): GeodeticCoordinates = this

    override fun asMgrsCoordinates(): MgrsCoordinates {
        val components = MGRSCoord
            .fromLatLon(latitude.asWorldWindAngle(), longitude.asWorldWindAngle())
            .toString()
            .split(" ")
        return MgrsCoordinates(
            zone = components[0].substring(0 until components[0].length - 3).toInt(),
            band = components[0].substring(components[0].length - 3 until components[0].length - 2),
            hundredKMSquareID = components[0].substring(components[0].length - 2),
            easting = components[1].toInt().meters,
            northing = components[2].toInt().meters
        )
    }

    override fun asUtmCoordinates(): UtmCoordinates {
        val worldWindUtmCoord = try {
            UTMCoord.fromLatLon(
                latitude.asWorldWindAngle(),
                longitude.asWorldWindAngle()
            )
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid UTM coordinates (might be too close to a pole")
        }
        return with(worldWindUtmCoord) {
            UtmCoordinates(
                zone,
                when (hemisphere) {
                    earth.worldwind.geom.coords.Hemisphere.N -> Hemisphere.NORTH
                    earth.worldwind.geom.coords.Hemisphere.S -> Hemisphere.SOUTH
                },
                easting.meters,
                northing.meters
            )
        }
    }

    override fun toString(): String = "$latitude, $longitude"
}

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
        this.inMeters().value.roundToInt().toString().padStart(NUMERICAL_LOCATION_FORMAT, ZERO)

    private companion object {
        private const val NUMERICAL_LOCATION_FORMAT = 5
        private const val ZERO = '0'
    }
}

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
data class UtmCoordinates(
    val zone: Int,
    val hemisphere: Hemisphere,
    val easting: Distance,
    val northing: Distance
) : Coordinates {

    override fun asGeodeticCoordinates(): GeodeticCoordinates =
        try {
            val worldWindUtmCoord = UTMCoord.fromUTM(
                zone,
                when (hemisphere) {
                    Hemisphere.NORTH -> earth.worldwind.geom.coords.Hemisphere.N
                    Hemisphere.SOUTH -> earth.worldwind.geom.coords.Hemisphere.S
                },
                easting.inMeters().value,
                northing.inMeters().value
            )
            GeodeticCoordinates(
                worldWindUtmCoord.latitude.asAngle(),
                worldWindUtmCoord.longitude.asAngle()
            )
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid UTM coordinates (might be too close to a pole")
        }

    override fun asMgrsCoordinates(): MgrsCoordinates = asGeodeticCoordinates().asMgrsCoordinates()

    override fun asUtmCoordinates(): UtmCoordinates = this

    override fun toString(): String {
        val eastingMeters = easting.inMeters().value.roundToInt()
        val northingMeters = northing.inMeters().value.roundToInt()
        return "$zone$hemisphere ${eastingMeters}m E ${northingMeters}m N"
    }
}

private fun Angle.asWorldWindAngle(): earth.worldwind.geom.Angle = inDegrees().value.degrees

private fun earth.worldwind.geom.Angle.asAngle(): Angle = Angle.Degrees(inDegrees)
