package io.trewartha.positional.data.location

import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import kotlinx.datetime.Instant

/**
 * Location somewhere on Earth, represented in a decimal degrees latitude, longitude, altitude, and
 * timestamp (among other metadata).
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracy: Distance?,
    val bearing: Angle?,
    val bearingAccuracy: Angle?,
    val altitude: Distance?,
    val altitudeAccuracy: Distance?,
    val speed: Speed?,
    val speedAccuracy: Speed?,
    val timestamp: Instant,
    val magneticDeclination: Angle
)
