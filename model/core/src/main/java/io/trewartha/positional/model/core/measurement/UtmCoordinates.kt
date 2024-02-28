package io.trewartha.positional.model.core.measurement

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
                easting.inMeters().magnitude,
                northing.inMeters().magnitude
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
        val eastingMeters = easting.inMeters().magnitude.roundToInt()
        val northingMeters = northing.inMeters().magnitude.roundToInt()
        return "$zone$hemisphere ${eastingMeters}m E ${northingMeters}m N"
    }
}
