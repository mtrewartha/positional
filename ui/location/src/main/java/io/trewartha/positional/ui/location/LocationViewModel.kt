package io.trewartha.positional.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.Locator
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.ui.core.flow.ForViewModel
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

    val state: StateFlow<LocationState> =
        combine(
            locator.location,
            settings.coordinatesFormat,
            settings.units,
            settings.locationAccuracyVisibility
        ) { location, coordinatesFormat, units, accuracyVisibility ->
            LocationState.Data(location, coordinatesFormat, units, accuracyVisibility)
        }.stateIn(
            viewModelScope,
            SharingStarted.ForViewModel,
            initialValue = LocationState.Loading
        )
}
