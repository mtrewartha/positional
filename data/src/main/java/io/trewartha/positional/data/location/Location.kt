package io.trewartha.positional.data.location

import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import kotlinx.datetime.Instant

/**
 * Location somewhere on Earth, represented in a decimal degrees latitude, longitude, altitude, and
 * timestamp (among other metadata).
 *
 * @property coordinates Geographic coordinates of the location
 * @property horizontalAccuracy Horizontal accuracy of the location or `null` if unknown
 * @property bearing Bearing (direction of movement) or `null` if unknown
 * @property bearingAccuracy Accuracy of the bearing or `null` if unknown
 * @property altitude Altitude above the WGS84 ellipsoid or `null` if unknown
 * @property altitudeAccuracy Accuracy of the altitude or `null` if unknown
 * @property magneticDeclination Magnetic declination (i.e. angle between true north and magnetic
 * north) at the location
 * @property speed Speed or `null` if unknown
 * @property speedAccuracy Accuracy of the speed or `null` if unknown
 * @property timestamp Instant at which the location was determined
 */
data class Location(
    val coordinates: Coordinates,
    val horizontalAccuracy: Distance?,
    val bearing: Angle?,
    val bearingAccuracy: Angle?,
    val altitude: Distance?,
    val altitudeAccuracy: Distance?,
    val magneticDeclination: Angle,
    val speed: Speed?,
    val speedAccuracy: Speed?,
    val timestamp: Instant
)
