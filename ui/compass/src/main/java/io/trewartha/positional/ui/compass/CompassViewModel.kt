package io.trewartha.positional.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.compass.Compass
import io.trewartha.positional.data.location.Locator
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.model.core.measurement.Angle
import io.trewartha.positional.ui.core.State
import io.trewartha.positional.ui.core.flow.ForViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompassViewModel @Inject constructor(
    compass: Compass?,
    locator: Locator,
    settings: SettingsRepository
) : ViewModel() {

    private val magneticDeclination: Flow<Angle?> = locator.location
        .map { it.magneticDeclination }
        .distinctUntilChanged()
        .onStart { emit(null) }

    val state: StateFlow<State<CompassData, CompassError>> =
        if (compass == null) {
            emptyFlow<State<CompassData, CompassError>>().stateIn(
                viewModelScope,
                SharingStarted.ForViewModel,
                initialValue = State.Error(CompassError.SensorsMissing)
            )
        } else {
            combine(
                compass.azimuth,
                magneticDeclination,
                settings.compassMode,
                settings.compassNorthVibration
            ) { reading, declination, mode, northVibration ->
                State.Loaded(CompassData(reading, declination, mode, northVibration))
            }.stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)
        }
}
