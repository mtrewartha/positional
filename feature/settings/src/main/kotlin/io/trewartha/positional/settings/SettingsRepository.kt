package io.trewartha.positional.settings

import io.trewartha.positional.core.measurement.Units
import kotlinx.coroutines.flow.Flow

/**
 * Repository for application settings
 */
public interface SettingsRepository {

    /**
     * Compass mode to use in the Compass view
     */
    public val compassMode: Flow<CompassMode>

    /**
     * Style of device vibration to trigger when the compass crosses north
     */
    public val compassNorthVibration: Flow<CompassNorthVibration>

    /**
     * Coordinates format to display location coordinates in
     */
    public val coordinatesFormat: Flow<CoordinatesFormat>

    /**
     * Visibility of location accuracies in the Location view
     */
    public val locationAccuracyVisibility: Flow<LocationAccuracyVisibility>

    /**
     * Theme to use for the application
     */
    public val theme: Flow<Theme>

    /**
     * Units to use for measurements
     */
    public val units: Flow<Units>

    /**
     * Sets the compass mode to use in the Compass view
     *
     * @param compassMode Compass mode to use
     */
    public suspend fun setCompassMode(compassMode: CompassMode)

    /**
     * Sets the style of device vibration to trigger when the compass crosses north
     *
     * @param compassNorthVibration Style of device vibration to use
     */
    public suspend fun setCompassNorthVibration(compassNorthVibration: CompassNorthVibration)

    /**
     * Sets the coordinates format to display location coordinates in
     *
     * @param coordinatesFormat Coordinates format to use
     */
    public suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat)

    /**
     * Sets the visibility of accuracies in the Location view
     *
     * @param visibility Visibility of location accuracies
     */
    public suspend fun setLocationAccuracyVisibility(visibility: LocationAccuracyVisibility)

    /**
     * Sets the theme to use for the application
     *
     * @param theme Theme to use
     */
    public suspend fun setTheme(theme: Theme)

    /**
     * Sets the units to use for measurements
     *
     * @param units Units to use
     */
    public suspend fun setUnits(units: Units)
}
