package io.trewartha.positional.data.settings

import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.model.settings.CompassNorthVibration
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility
import io.trewartha.positional.model.settings.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * [SettingsRepository] implementation for use in testing classes that depend on one
 */
class TestSettingsRepository : SettingsRepository {

    override val compassMode: Flow<CompassMode>
        get() = _compassMode.distinctUntilChanged()
    private val _compassMode = MutableSharedFlow<CompassMode>(replay = 1)

    override val compassNorthVibration: Flow<CompassNorthVibration>
        get() = _compassNorthVibration.distinctUntilChanged()
    private val _compassNorthVibration = MutableSharedFlow<CompassNorthVibration>(replay = 1)

    override val coordinatesFormat: Flow<CoordinatesFormat>
        get() = _coordinatesFormat.distinctUntilChanged()
    private val _coordinatesFormat = MutableSharedFlow<CoordinatesFormat>(replay = 1)

    override val locationAccuracyVisibility: Flow<LocationAccuracyVisibility>
        get() = _locationAccuracyVisibility.distinctUntilChanged()
    private val _locationAccuracyVisibility =
        MutableSharedFlow<LocationAccuracyVisibility>(replay = 1)

    override val theme: Flow<Theme>
        get() = _theme.distinctUntilChanged()
    private val _theme = MutableSharedFlow<Theme>(replay = 1)

    override val units: Flow<Units>
        get() = _units.distinctUntilChanged()
    private val _units = MutableSharedFlow<Units>(replay = 1)

    override suspend fun setCompassMode(compassMode: CompassMode) {
        _compassMode.emit(compassMode)
    }

    override suspend fun setCompassNorthVibration(compassNorthVibration: CompassNorthVibration) {
        _compassNorthVibration.emit(compassNorthVibration)
    }

    override suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat) {
        _coordinatesFormat.emit(coordinatesFormat)
    }

    override suspend fun setLocationAccuracyVisibility(visibility: LocationAccuracyVisibility) {
        _locationAccuracyVisibility.emit(visibility)
    }

    override suspend fun setTheme(theme: Theme) {
        _theme.emit(theme)
    }

    override suspend fun setUnits(units: Units) {
        _units.emit(units)
    }
}
