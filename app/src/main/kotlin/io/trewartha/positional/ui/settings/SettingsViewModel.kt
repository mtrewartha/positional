package io.trewartha.positional.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.data.ui.LocationAccuracyVisibility
import io.trewartha.positional.data.ui.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val compassMode: Flow<CompassMode> = settingsRepository.compassMode

    val coordinatesFormat: Flow<CoordinatesFormat> = settingsRepository.coordinatesFormat

    val locationAccuracyVisibility: Flow<LocationAccuracyVisibility> =
        settingsRepository.locationAccuracyVisibility

    val theme: Flow<Theme> = settingsRepository.theme

    val units: Flow<Units> = settingsRepository.units

    fun onCompassModeChange(compassMode: CompassMode) {
        viewModelScope.launch { settingsRepository.setCompassMode(compassMode) }
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
