package io.trewartha.positional.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val compassMode: StateFlow<CompassMode?> = settingsRepository.compassMode
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    val compassNorthVibration: StateFlow<CompassNorthVibration?> =
        settingsRepository.compassNorthVibration
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    val coordinatesFormat: StateFlow<CoordinatesFormat?> = settingsRepository.coordinatesFormat
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    val locationAccuracyVisibility: StateFlow<LocationAccuracyVisibility?> =
        settingsRepository.locationAccuracyVisibility
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    val theme: StateFlow<Theme?> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    val units: StateFlow<Units?> = settingsRepository.units
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = null)

    fun onCompassModeChange(compassMode: CompassMode) {
        viewModelScope.launch { settingsRepository.setCompassMode(compassMode) }
    }

    fun onCompassNorthVibrationChange(compassNorthVibration: CompassNorthVibration) {
        viewModelScope.launch { settingsRepository.setCompassNorthVibration(compassNorthVibration) }
    }

    fun onCoordinatesFormatChange(coordinatesFormat: CoordinatesFormat) {
        viewModelScope.launch { settingsRepository.setCoordinatesFormat(coordinatesFormat) }
    }

    fun onLocationAccuracyVisibilityChange(visibility: LocationAccuracyVisibility) {
        viewModelScope.launch { settingsRepository.setLocationAccuracyVisibility(visibility) }
    }

    fun onThemeChange(theme: Theme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun onUnitsChange(units: Units) {
        viewModelScope.launch { settingsRepository.setUnits(units) }
    }
}
