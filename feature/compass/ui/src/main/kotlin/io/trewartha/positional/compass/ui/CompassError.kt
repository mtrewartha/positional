package io.trewartha.positional.compass.ui

import androidx.compose.runtime.Immutable

@Immutable
sealed interface CompassError {

    @Immutable
    data object SensorsMissing : CompassError
}
