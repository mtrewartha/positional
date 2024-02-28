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

    override fun asGeodeticCoordinates(): GeodeticCoordinates = this

    override fun asMgrsCoordinates(): MgrsCoordinates {
        val components = MGRSCoord.fromLatLon(
            latitude.asWorldWindAngle(),
            longitude.asWorldWindAngle()
        )
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
