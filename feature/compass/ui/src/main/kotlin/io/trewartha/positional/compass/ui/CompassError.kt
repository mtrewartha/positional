package io.trewartha.positional.compass.ui

import androidx.compose.runtime.Immutable

@Immutable
public sealed interface CompassError {

    @Immutable
    public data object SensorsMissing : CompassError
}
