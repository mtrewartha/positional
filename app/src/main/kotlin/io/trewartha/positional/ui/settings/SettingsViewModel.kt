package io.trewartha.positional.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val compassMode: Flow<CompassMode> = settingsRepository.compassMode

    val coordinatesFormat: Flow<CoordinatesFormat> = settingsRepository.coordinatesFormat

    val showAccuracies: Flow<Boolean> = settingsRepository.showAccuracies

    val theme: Flow<Theme> = settingsRepository.theme

    val units: Flow<Units> = settingsRepository.units

    fun onCompassModeChange(compassMode: CompassMode) {
        viewModelScope.launch { settingsRepository.setCompassMode(compassMode) }
    }

    fun onCoordinatesFormatChange(coordinatesFormat: CoordinatesFormat) {
        viewModelScope.launch { settingsRepository.setCoordinatesFormat(coordinatesFormat) }
    }

    fun onShowAccuraciesChange(showAccuracies: Boolean) {
        viewModelScope.launch { settingsRepository.setShowAccuracies(showAccuracies) }
    }

    fun onThemeChange(theme: Theme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun onUnitsChange(units: Units) {
        viewModelScope.launch { settingsRepository.setUnits(units) }
    }
}
