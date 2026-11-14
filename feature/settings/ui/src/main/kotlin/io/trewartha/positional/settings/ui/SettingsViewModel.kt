package io.trewartha.positional.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.trewartha.positional.AppScope
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.ui.flow.ForViewModel
import io.trewartha.positional.settings.CompassMode
import io.trewartha.positional.settings.CompassNorthVibration
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility
import io.trewartha.positional.settings.SettingsRepository
import io.trewartha.positional.settings.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Exposes the user settings and persists changes to them
 */
@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
public class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /** The current compass mode, or null until loaded */
    public val compassMode: StateFlow<CompassMode?> = settingsRepository.compassMode
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /** The current compass north vibration setting, or null until loaded */
    public val compassNorthVibration: StateFlow<CompassNorthVibration?> =
        settingsRepository.compassNorthVibration
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /** The current coordinates format, or null until loaded */
    public val coordinatesFormat: StateFlow<CoordinatesFormat?> =
        settingsRepository.coordinatesFormat
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /** The current location accuracy visibility, or null until loaded */
    public val locationAccuracyVisibility: StateFlow<LocationAccuracyVisibility?> =
        settingsRepository.locationAccuracyVisibility
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /** The current theme, or null until loaded */
    public val theme: StateFlow<Theme?> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /** The current units, or null until loaded */
    public val units: StateFlow<Units?> = settingsRepository.units
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    /**
     * Persists a new value for the compass mode
     */
    public fun onCompassModeChange(compassMode: CompassMode) {
        viewModelScope.launch { settingsRepository.setCompassMode(compassMode) }
    }

    /**
     * Persists a new value for the compass north vibration setting
     */
    public fun onCompassNorthVibrationChange(compassNorthVibration: CompassNorthVibration) {
        viewModelScope.launch { settingsRepository.setCompassNorthVibration(compassNorthVibration) }
    }

    /**
     * Persists a new value for the coordinates format
     */
    public fun onCoordinatesFormatChange(coordinatesFormat: CoordinatesFormat) {
        viewModelScope.launch { settingsRepository.setCoordinatesFormat(coordinatesFormat) }
    }

    /**
     * Persists a new value for the location accuracy visibility
     */
    public fun onLocationAccuracyVisibilityChange(visibility: LocationAccuracyVisibility) {
        viewModelScope.launch { settingsRepository.setLocationAccuracyVisibility(visibility) }
    }

    /**
     * Persists a new value for the theme
     */
    public fun onThemeChange(theme: Theme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    /**
     * Persists a new value for the units
     */
    public fun onUnitsChange(units: Units) {
        viewModelScope.launch { settingsRepository.setUnits(units) }
    }
}
