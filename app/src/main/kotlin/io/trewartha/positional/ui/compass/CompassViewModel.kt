package io.trewartha.positional.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.compass.CompassReadings
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.domain.compass.CompassHardwareException
import io.trewartha.positional.domain.compass.GetCompassDeclinationUseCase
import io.trewartha.positional.domain.compass.GetCompassModeUseCase
import io.trewartha.positional.domain.compass.GetCompassReadingsUseCase
import io.trewartha.positional.ui.utils.ForViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompassViewModel @Inject constructor(
    getCompassDeclinationUseCase: GetCompassDeclinationUseCase,
    getCompassModeUseCase: GetCompassModeUseCase,
    getCompassReadingsUseCase: GetCompassReadingsUseCase,
) : ViewModel() {

    val state: StateFlow<State> =
        combine<Angle, CompassMode, CompassReadings, State>(
            getCompassDeclinationUseCase(),
            getCompassModeUseCase(),
            getCompassReadingsUseCase(),
        ) { declination, mode, readings ->
            State.SensorsPresent.Loaded(
                rotationMatrix = readings.rotationMatrix,
                accelerometerAccuracy = readings.accelerometerAccuracy,
                magnetometerAccuracy = readings.magnetometerAccuracy,
                magneticDeclination = declination,
                mode = mode,
            )
        }.catch { throwable ->
            if (throwable is CompassHardwareException) {
                emit(State.SensorsMissing)
            } else {
                throw throwable
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.ForViewModel,
            initialValue = State.SensorsPresent.Loading,
        )

    sealed interface State {

        object SensorsMissing : State

        sealed interface SensorsPresent : State {

            object Loading : SensorsPresent

            data class Loaded(
                val rotationMatrix: FloatArray,
                val accelerometerAccuracy: CompassAccuracy?,
                val magnetometerAccuracy: CompassAccuracy?,
                val magneticDeclination: Angle,
                val mode: CompassMode,
            ) : SensorsPresent {
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as Loaded

                    if (!rotationMatrix.contentEquals(other.rotationMatrix)) return false
                    if (accelerometerAccuracy != other.accelerometerAccuracy) return false
                    if (magnetometerAccuracy != other.magnetometerAccuracy) return false
                    if (magneticDeclination != other.magneticDeclination) return false
                    return mode == other.mode
                }

                override fun hashCode(): Int {
                    var result = rotationMatrix.contentHashCode()
                    result = 31 * result + (accelerometerAccuracy?.hashCode() ?: 0)
                    result = 31 * result + (magnetometerAccuracy?.hashCode() ?: 0)
                    result = 31 * result + magneticDeclination.hashCode()
                    result = 31 * result + mode.hashCode()
                    return result
                }
            }
        }
    }
}
