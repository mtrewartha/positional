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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationViewModel @Inject constructor(
    getLocationUseCase: GetLocationUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val coordinatesFormat = settingsRepository.coordinatesFormat
    private val location = getLocationUseCase().throttleFirst(locationFlowPeriod)
    private val accuraciesShown = settingsRepository.showAccuracies
    private val units = settingsRepository.units

    val state: StateFlow<LocationState> =
        combine(
            location,
            coordinatesFormat,
            units,
            accuraciesShown
        ) { location, coordinatesFormat, units, accuraciesShown ->
            createState(location, coordinatesFormat, units, accuraciesShown)
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
                showAccuracies = false
            )
        )

    private fun createState(
        location: Location,
        coordinatesFormat: CoordinatesFormat,
        units: Units,
        accuraciesShown: Boolean
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
        showAccuracies = accuraciesShown
    )
}

private val locationFlowPeriod = 2.seconds
