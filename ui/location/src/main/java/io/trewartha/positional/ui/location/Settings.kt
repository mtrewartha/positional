package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility

@Immutable
data class Settings(
    val coordinatesFormat: CoordinatesFormat,
    val units: Units,
    val accuracyVisibility: LocationAccuracyVisibility
)
