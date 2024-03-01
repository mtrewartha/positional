package io.trewartha.positional.model.core.measurement

import earth.worldwind.geom.coords.MGRSCoord
import earth.worldwind.geom.coords.UTMCoord

/**
 * Geodetic coordinates with latitude and longitude both given in decimal degrees. See this
 * [Wikipedia article](https://en.wikipedia.org/wiki/Geodetic_coordinates) for more information.
 *
 * @property latitude Angle of latitude
 * @property longitude Angle of longitude
 */
data class GeodeticCoordinates(val latitude: Angle, val longitude: Angle) : Coordinates {

    private val isPolar: Boolean
        get() = latitude.inDegrees().magnitude !in UTM_SOUTHERN_BOUND..UTM_NORTHERN_BOUND

    override fun asGeodeticCoordinates(): GeodeticCoordinates = this

    @Suppress("MagicNumber")
    override fun asMgrsCoordinates(): MgrsCoordinates {
        val components = MGRSCoord
            .fromLatLon(latitude.asWorldWindAngle(), longitude.asWorldWindAngle())
            .toString()
            .trim()
            .split(" ")
        return if (isPolar) {
            MgrsCoordinates(
                gridZoneDesignator = components[0].substring(0..0), // A or B for south pole, Y or Z for north pole
                gridSquareID = components[0].substring(1..2), // Two letters for 100km square ID
                easting = components[1].toInt().meters,
                northing = components[2].toInt().meters
            )
        } else {
            val zone = components[0].substring(0..1).toInt() // Two digits for UTM zone
            val latitudeBand = components[0].substring(2..2) // One letter for MGRS latitude band
            val hundredKID = components[0].substring(3..4) // Two letters for 100 km square ID
            MgrsCoordinates(
                gridZoneDesignator = "$zone$latitudeBand",
                gridSquareID = hundredKID,
                easting = components[1].toInt().meters,
                northing = components[2].toInt().meters
            )
        }
    }

    override fun asUtmCoordinates(): UtmCoordinates? {
        if (isPolar) return null
        val worldWindUtmCoord = UTMCoord.fromLatLon(
            latitude.asWorldWindAngle(),
            longitude.asWorldWindAngle()
        )
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

    override fun toString(): String = FORMAT.format(
        latitude.inDegrees().magnitude,
        longitude.inDegrees().magnitude
    )
}

private const val FORMAT = "%.5f, %.5f"
private const val UTM_NORTHERN_BOUND = 84.0
private const val UTM_SOUTHERN_BOUND = -80.0
