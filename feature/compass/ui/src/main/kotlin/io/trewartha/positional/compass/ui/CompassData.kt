package io.trewartha.positional.compass.ui

import androidx.compose.runtime.Immutable
import io.trewartha.positional.compass.Azimuth
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.settings.CompassMode
import io.trewartha.positional.settings.CompassNorthVibration

@Immutable
public data class CompassData(
    val azimuth: Azimuth,
    val declination: Angle?,
    val mode: CompassMode,
    val northVibration: CompassNorthVibration
)
