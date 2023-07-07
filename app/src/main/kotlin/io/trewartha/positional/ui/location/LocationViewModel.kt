package io.trewartha.positional.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.utils.flow.throttleFirst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationViewModel @Inject constructor(
    coroutineContext: CoroutineContext,
    getLocationUseCase: GetLocationUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val coordinatesFormat = settingsRepository.coordinatesFormat
    private val _events = Channel<LocationEvent>(UNLIMITED)
    private val location = getLocationUseCase().throttleFirst(locationFlowPeriod)
    private val screenLockedOn = settingsRepository.lockScreenOn
    private val accuraciesShown = settingsRepository.showAccuracies
    private val units = settingsRepository.units

    val events: SendChannel<LocationEvent>
        get() = _events

    val state: StateFlow<LocationState> =
        combine(
            location,
            coordinatesFormat,
            units,
            accuraciesShown,
            screenLockedOn
        ) { location, coordinatesFormat, units, accuraciesShown, screenLockedOn ->
            createState(location, coordinatesFormat, units, accuraciesShown, screenLockedOn)
        }.flowOn(Dispatchers.Default).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            initialValue = LocationState(
                coordinates = null,
                coordinatesFormat = CoordinatesFormat.DD,
                timestamp = null,
                horizontalAccuracy = null,
                bearing = null,
                bearingAccuracy = null,
                altitude = null,
                altitudeAccuracy = null,
                speed = null,
                speedAccuracy = null,
                units = null,
                showAccuracies = false,
                screenLockedOn = false
            )
        )

    init {
        viewModelScope.launch(coroutineContext) { consumeEvents() }
    }

    private fun createState(
        location: Location,
        coordinatesFormat: CoordinatesFormat,
        units: Units,
        accuraciesShown: Boolean,
        screenLockedOn: Boolean
    ) = LocationState(
        coordinates = location.coordinates,
        coordinatesFormat = coordinatesFormat,
        timestamp = location.timestamp,
        horizontalAccuracy = location.horizontalAccuracy,
        bearing = location.bearing,
        bearingAccuracy = location.bearingAccuracy,
        altitude = location.altitude,
        altitudeAccuracy = location.altitudeAccuracy,
        speed = location.speed,
        speedAccuracy = location.speedAccuracy,
        units = units,
        showAccuracies = accuraciesShown,
        screenLockedOn = screenLockedOn
    )

    private suspend fun consumeEvents() {
        _events.consumeAsFlow().collect { event ->
            when (event) {
                is LocationEvent.LockToggle -> consumeLockToggle(event)
            }
        }
    }

    private suspend fun consumeLockToggle(event: LocationEvent.LockToggle) {
        settingsRepository.setLockScreenOn(event.locked)
    }
}

private val locationFlowPeriod = 2.seconds
