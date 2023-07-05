package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.units.Units
import kotlinx.datetime.Instant

@Immutable
data class LocationState(
    val coordinates: Coordinates?,
    val coordinatesFormat: CoordinatesFormat,
    val timestamp: Instant?,
    val horizontalAccuracyMeters: Float?,
    val bearingDegrees: Float?,
    val bearingAccuracyDegrees: Float?,
    val altitudeMeters: Float?,
    val altitudeAccuracyMeters: Float?,
    val speedMetersPerSecond: Float?,
    val speedAccuracyMetersPerSecond: Float?,
    val units: Units?,
    val showAccuracies: Boolean,
    val screenLockedOn: Boolean,
)
