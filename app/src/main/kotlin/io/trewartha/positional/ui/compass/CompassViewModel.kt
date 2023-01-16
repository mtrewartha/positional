package io.trewartha.positional.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.compass.CompassReadings
import io.trewartha.positional.domain.compass.CompassHardwareException
import io.trewartha.positional.domain.compass.GetCompassDeclinationUseCase
import io.trewartha.positional.domain.compass.GetCompassModeUseCase
import io.trewartha.positional.domain.compass.GetCompassReadingsUseCase
import io.trewartha.positional.ui.utils.ForViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CompassViewModel @Inject constructor(
    getCompassDeclinationUseCase: GetCompassDeclinationUseCase,
    getCompassModeUseCase: GetCompassModeUseCase,
    getCompassReadingsUseCase: GetCompassReadingsUseCase,
) : ViewModel() {

    private val sensorsMissingDetailsVisibleFlow = MutableStateFlow(false)

    val state: StateFlow<State> =
        combine<Float, CompassMode, CompassReadings, State>(
            getCompassDeclinationUseCase(),
            getCompassModeUseCase(),
            getCompassReadingsUseCase(),
        ) { declination, mode, readings ->
            State.SensorsPresent.Loaded(
                rotationMatrix = readings.rotationMatrix,
                accelerometerAccuracy = readings.accelerometerAccuracy,
                magnetometerAccuracy = readings.magnetometerAccuracy,
                magneticDeclinationDegrees = declination,
                mode = mode,
            )
        }.catch { throwable ->
            if (throwable is CompassHardwareException) {
                emit(State.SensorsMissing(detailsVisible = false))
            } else {
                throw throwable
            }
        }.combine(sensorsMissingDetailsVisibleFlow) { state, detailsVisible ->
            if (state is State.SensorsMissing) {
                state.copy(detailsVisible = detailsVisible)
            } else {
                state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.ForViewModel,
            initialValue = State.SensorsPresent.Loading,
        )

    fun onSensorsMissingWhyClick() {
        sensorsMissingDetailsVisibleFlow.update { true }
    }

    sealed interface State {
        data class SensorsMissing(val detailsVisible: Boolean) : State
        sealed interface SensorsPresent : State {
            object Loading : SensorsPresent
            data class Loaded(
                val rotationMatrix: FloatArray,
                val accelerometerAccuracy: CompassAccuracy?,
                val magnetometerAccuracy: CompassAccuracy?,
                val magneticDeclinationDegrees: Float,
                val mode: CompassMode,
            ) : SensorsPresent {
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as Loaded

                    if (!rotationMatrix.contentEquals(other.rotationMatrix)) return false
                    if (accelerometerAccuracy != other.accelerometerAccuracy) return false
                    if (magnetometerAccuracy != other.magnetometerAccuracy) return false
                    if (magneticDeclinationDegrees != other.magneticDeclinationDegrees) return false
                    if (mode != other.mode) return false

                    return true
                }

                override fun hashCode(): Int {
                    var result = rotationMatrix.contentHashCode()
                    result = 31 * result + (accelerometerAccuracy?.hashCode() ?: 0)
                    result = 31 * result + (magnetometerAccuracy?.hashCode() ?: 0)
                    result = 31 * result + magneticDeclinationDegrees.hashCode()
                    result = 31 * result + mode.hashCode()
                    return result
                }
            }
        }
    }
}
