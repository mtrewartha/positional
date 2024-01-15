package io.trewartha.positional.ui.compass

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.compass.Azimuth
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.ui.CompassNorthVibration

@Immutable
sealed interface CompassState {

    data object SensorsMissing : CompassState

    sealed interface SensorsPresent : CompassState

    data object Loading : SensorsPresent

    data class Data(
        val azimuth: Azimuth,
        val declination: Angle?,
        val mode: CompassMode,
        val northVibration: CompassNorthVibration
    ) : SensorsPresent
}
