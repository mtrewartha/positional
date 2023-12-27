package io.trewartha.positional.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.utils.flow.throttleFirst
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationViewModel @Inject constructor(
    getLocationUseCase: GetLocationUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val coordinatesFormat: StateFlow<CoordinatesFormat?> =
        settingsRepository.coordinatesFormat
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = null)

    val location: StateFlow<Location?> =
        getLocationUseCase().throttleFirst(locationFlowPeriod)
            .retry { cause ->
                if (cause is SecurityException) {
                    Timber.w("Waiting for location permissions to be granted")
                    delay(1.seconds)
                    true
                } else {
                    throw cause
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = null)

    val showAccuracies: StateFlow<Boolean?> =
        settingsRepository.showAccuracies
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = null)

    val units: StateFlow<Units?> =
        settingsRepository.units
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = null)
}

private val locationFlowPeriod = 2.seconds
