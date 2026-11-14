package io.trewartha.positional.location.ui

import androidx.compose.runtime.Immutable
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility

/**
 * The user settings that affect how location is presented
 *
 * @property coordinatesFormat format in which coordinates are shown
 * @property units units in which distances and speeds are shown
 * @property accuracyVisibility whether accuracy information is shown
 */
@Immutable
public data class Settings(
    val coordinatesFormat: CoordinatesFormat,
    val units: Units,
    val accuracyVisibility: LocationAccuracyVisibility
)
