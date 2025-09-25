package io.trewartha.positional.location.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.core.ui.asStates
import io.trewartha.positional.core.ui.flow.ForViewModel
import io.trewartha.positional.location.Location
import io.trewartha.positional.location.Locator
import io.trewartha.positional.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
public class LocationViewModel @Inject constructor(
    locator: Locator,
    settings: SettingsRepository
) : ViewModel() {

    public val location: StateFlow<State<Location, Unit>> = locator.location
        .asStates()
        .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)

    public val settings: StateFlow<State<Settings, Unit>> =
        combine(
            settings.coordinatesFormat,
            settings.units,
            settings.locationAccuracyVisibility
        ) { coordinatesFormat, units, accuracyVisibility ->
            Settings(coordinatesFormat, units, accuracyVisibility)
        }.asStates()
            .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)
}
