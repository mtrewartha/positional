package io.trewartha.positional.location.ui

import androidx.compose.runtime.Immutable
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility

@Immutable
public data class Settings(
    val coordinatesFormat: CoordinatesFormat,
    val units: Units,
    val accuracyVisibility: LocationAccuracyVisibility
)
