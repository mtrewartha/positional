package io.trewartha.positional.data.settings

import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.ui.LocationAccuracyVisibility
import io.trewartha.positional.data.ui.Theme
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
