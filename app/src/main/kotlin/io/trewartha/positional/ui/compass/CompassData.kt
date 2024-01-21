package io.trewartha.positional.ui.compass

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.compass.Azimuth
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.ui.CompassNorthVibration

@Immutable
data class CompassData(
    val azimuth: Azimuth,
    val declination: Angle?,
    val mode: CompassMode,
    val northVibration: CompassNorthVibration
)
