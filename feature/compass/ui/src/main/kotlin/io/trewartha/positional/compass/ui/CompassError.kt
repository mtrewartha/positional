package io.trewartha.positional.compass.ui

import androidx.compose.runtime.Immutable

/**
 * Reasons the compass cannot show a reading
 */
@Immutable
public sealed interface CompassError {

    /**
     * The device lacks the sensors required to compute a compass reading.
     */
    @Immutable
    public data object SensorsMissing : CompassError
}
