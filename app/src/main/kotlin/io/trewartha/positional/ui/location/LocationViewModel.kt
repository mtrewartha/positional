package io.trewartha.positional.ui.location

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.utils.flow.throttleFirst
import io.trewartha.positional.ui.utils.ForViewModel
import io.trewartha.positional.ui.utils.format.LocationFormatter
import io.trewartha.positional.ui.utils.mutableSharedViewModelEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val app: Application,
    private val clipboardManager: ClipboardManager,
    private val locationFormatter: LocationFormatter,
    getLocationUseCase: GetLocationUseCase,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(app) {

    private val accuracyVisibility: Flow<Boolean> =
        settingsRepository.hideAccuracies.map { !it }

    private val coordinatesFormat: Flow<CoordinatesFormat> =
        settingsRepository.coordinatesFormat

    private val location: StateFlow<Location?> = getLocationUseCase()
        .throttleFirst(LOCATION_FLOW_PERIOD)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = null)

    private val units: Flow<Units> = settingsRepository.units

    private val screenLockEnabledFlow: StateFlow<Boolean> = settingsRepository.screenLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val state: StateFlow<LocationState?> = combine(
        accuracyVisibility,
        location.filterNotNull(),
        coordinatesFormat,
        screenLockEnabledFlow,
        units,
    ) { showAccuracies, location, coordinatesFormat, screenLockEnabled, units ->
        val accuracy = locationFormatter.getCoordinatesAccuracy(location, units)
        val (coordinates, maxLines) = locationFormatter.getCoordinates(location, coordinatesFormat)
        val coordinatesForCopy =
            locationFormatter.getCoordinatesForCopy(location, coordinatesFormat)
        val bearing = locationFormatter.getBearing(location)
        val bearingAccuracy =
            if (showAccuracies) locationFormatter.getBearingAccuracy(location) else null
        val altitude = locationFormatter.getAltitude(location, units)
        val altitudeAccuracy =
            if (showAccuracies) locationFormatter.getAltitudeAccuracy(location, units) else null
        val speed = locationFormatter.getSpeed(location, units)
        val speedAccuracy =
            if (showAccuracies) locationFormatter.getSpeedAccuracy(location, units) else null
        val updatedAt = locationFormatter.getTimestamp(location)

        LocationState(
            coordinates = coordinates,
            maxLines = maxLines,
            coordinatesForCopy = coordinatesForCopy,
            stats = LocationState.Stats(
                accuracy = accuracy,
                bearing = bearing,
                bearingAccuracy = bearingAccuracy,
                altitude = altitude,
                altitudeAccuracy = altitudeAccuracy,
                speed = speed,
                speedAccuracy = speedAccuracy,
                showAccuracies = showAccuracies,
                updatedAt = updatedAt,
            ),
            screenLockEnabled = screenLockEnabled,
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.ForViewModel,
        initialValue = null
    )

    val events: Flow<LocationEvent>
        get() = _events
    private val _events = mutableSharedViewModelEventFlow<LocationEvent>()

    fun onCopyClick() {
        viewModelScope.launch {
            val coordinates = state.value?.coordinatesForCopy
            val event = if (coordinates.isNullOrBlank()) {
                LocationEvent.ShowCoordinatesCopyErrorSnackbar
            } else {
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        app.getString(R.string.location_coordinates_copy_label),
                        coordinates
                    )
                )
                LocationEvent.ShowCoordinatesCopySuccessBothSnackbar
            }
            _events.emit(event)
        }
    }

    fun onLaunchClick() {
        viewModelScope.launch {
            val currentLocation = location.value ?: return@launch
            _events.emit(
                LocationEvent.NavigateToGeoActivity(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    currentLocation.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        }
    }

    fun onScreenLockCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            settingsRepository.setScreenLockEnabled(checked)
            val event = if (checked)
                LocationEvent.ShowScreenLockedSnackbar
            else
                LocationEvent.ShowScreenUnlockedSnackbar
            _events.emit(event)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            val coordinates = state.value?.coordinates
            val event = if (coordinates.isNullOrBlank())
                LocationEvent.ShowCoordinatesShareErrorSnackbar
            else
                LocationEvent.ShowCoordinatesShareSheet(coordinates)
            _events.emit(event)
        }
    }

    companion object {
        private val LOCATION_FLOW_PERIOD = 2.seconds
    }
}
