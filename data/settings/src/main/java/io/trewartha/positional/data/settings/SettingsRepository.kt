package io.trewartha.positional.data.settings

import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.model.settings.CompassNorthVibration
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility
import io.trewartha.positional.model.settings.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Repository for application settings
 */
interface SettingsRepository {

    /**
     * Compass mode to use in the Compass view
     */
    val compassMode: Flow<CompassMode>

    /**
     * Style of device vibration to trigger when the compass crosses north
     */
    val compassNorthVibration: Flow<CompassNorthVibration>

    /**
     * Coordinates format to display location coordinates in
     */
    val coordinatesFormat: Flow<CoordinatesFormat>

    /**
     * Visibility of location accuracies in the Location view
     */
    val locationAccuracyVisibility: Flow<LocationAccuracyVisibility>

    /**
     * Theme to use for the application
     */
    val theme: Flow<Theme>

    /**
     * Units to use for measurements
     */
    val units: Flow<Units>

    /**
     * Sets the compass mode to use in the Compass view
     *
     * @param compassMode Compass mode to use
     */
    suspend fun setCompassMode(compassMode: CompassMode)

    /**
     * Sets the style of device vibration to trigger when the compass crosses north
     *
     * @param compassNorthVibration Style of device vibration to use
     */
    suspend fun setCompassNorthVibration(compassNorthVibration: CompassNorthVibration)

    /**
     * Sets the coordinates format to display location coordinates in
     *
     * @param coordinatesFormat Coordinates format to use
     */
    suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat)

    /**
     * Sets the visibility of accuracies in the Location view
     *
     * @param visibility Visibility of location accuracies
     */
    suspend fun setLocationAccuracyVisibility(visibility: LocationAccuracyVisibility)

    /**
     * Sets the theme to use for the application
     *
     * @param theme Theme to use
     */
    suspend fun setTheme(theme: Theme)

    /**
     * Sets the units to use for measurements
     *
     * @param units Units to use
     */
    suspend fun setUnits(units: Units)
}
