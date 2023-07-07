package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import kotlinx.datetime.Instant

@Immutable
data class LocationState(
    val coordinates: Coordinates?,
    val coordinatesFormat: CoordinatesFormat,
    val timestamp: Instant?,
    val horizontalAccuracy: Distance?,
    val bearing: Angle?,
    val bearingAccuracy: Angle?,
    val altitude: Distance?,
    val altitudeAccuracy: Distance?,
    val speed: Speed?,
    val speedAccuracy: Speed?,
    val units: Units?,
    val showAccuracies: Boolean,
    val screenLockedOn: Boolean,
)
