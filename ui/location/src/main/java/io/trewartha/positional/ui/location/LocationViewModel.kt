package io.trewartha.positional.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.Locator
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.ui.core.State
import io.trewartha.positional.ui.core.flow.ForViewModel
import io.trewartha.positional.ui.core.asStates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    locator: Locator,
    settings: SettingsRepository
) : ViewModel() {

    val location: StateFlow<State<Location, Unit>> = locator.location
        .asStates()
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)

    val settings: StateFlow<State<Settings, Unit>> =
        combine(
            settings.coordinatesFormat,
            settings.units,
            settings.locationAccuracyVisibility
        ) { coordinatesFormat, units, accuracyVisibility ->
            Settings(coordinatesFormat, units, accuracyVisibility)
        }.asStates()
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)
}
