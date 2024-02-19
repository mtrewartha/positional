package io.trewartha.positional.ui.compass

import androidx.compose.runtime.Immutable

@Immutable
sealed interface CompassError {

    @Immutable
    data object SensorsMissing : CompassError
}
