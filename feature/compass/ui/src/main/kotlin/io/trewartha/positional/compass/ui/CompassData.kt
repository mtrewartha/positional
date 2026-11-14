package io.trewartha.positional.compass.ui

import androidx.compose.runtime.Immutable
import io.trewartha.positional.compass.Azimuth
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.settings.CompassMode
import io.trewartha.positional.settings.CompassNorthVibration

/**
 * Everything needed to render a compass reading
 *
 * @property azimuth direction the device is facing
 * @property declination angle between magnetic and true north, or null if unknown
 * @property mode whether the compass reports true or magnetic north
 * @property northVibration whether the device vibrates when facing north
 */
@Immutable
public data class CompassData(
    val azimuth: Azimuth,
    val declination: Angle?,
    val mode: CompassMode,
    val northVibration: CompassNorthVibration
)
