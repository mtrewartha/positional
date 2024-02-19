package io.trewartha.positional.ui.compass

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.compass.Azimuth
import io.trewartha.positional.model.core.measurement.Angle
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.model.settings.CompassNorthVibration

@Immutable
data class CompassData(
    val azimuth: Azimuth,
    val declination: Angle?,
    val mode: CompassMode,
    val northVibration: CompassNorthVibration
)
