package io.trewartha.positional.ui.compass

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.Compass
import io.trewartha.positional.domain.entities.Location
import io.trewartha.positional.domain.entities.Locator
import io.trewartha.positional.domain.utils.flow.throttleFirst
import io.trewartha.positional.ui.utils.ForViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class CompassViewModel @Inject constructor(
    application: Application,
    compass: Compass?,
    locator: Locator,
    private val prefs: SharedPreferences,
) : AndroidViewModel(application) {

    private val magneticDeclinationDegreesFlow: Flow<Float> =
        (locator.locationFlow as Flow<Location?>)
            .filterNotNull()
            .throttleFirst(MAGNETIC_DECLINATION_LOCATION_THROTTLE_PERIOD)
            .map { it.magneticDeclinationDegrees }

    private val modeFlow: Flow<CompassMode> =
        callbackFlow {
            if (prefs.contains(prefsKeyCompassMode))
                trySend(prefs.getString(prefsKeyCompassMode, null))
            prefCompassModeListener = PrefCompassModeListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefCompassModeListener)
            awaitClose {
                prefCompassModeListener
                    ?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map {
            when (it) {
                compassModePrefValueMagneticNorth -> CompassMode.MAGNETIC_NORTH
                compassModePrefValueTrueNorth -> CompassMode.TRUE_NORTH
                else -> CompassMode.TRUE_NORTH
            }
        }

    private val sensorsMissingDetailsVisibleFlow = MutableStateFlow(false)

    val state: StateFlow<State> =
        if (compass == null) {
            sensorsMissingDetailsVisibleFlow.map { State.SensorsMissing(it) }
        } else {
            combine(
                compass.rotationMatrixFlow,
                compass.accelerometerAccuracyFlow,
                compass.magnetometerAccuracyFlow,
                magneticDeclinationDegreesFlow,
                modeFlow
            ) { rotationMatrix,
                accelerometerAccuracy,
                magnetometerAccuracy,
                magneticDeclinationDegrees,
                mode ->
                State.SensorsPresent.Loaded(
                    rotationMatrix = rotationMatrix,
                    accelerometerAccuracy = accelerometerAccuracy,
                    magnetometerAccuracy = magnetometerAccuracy,
                    magneticDeclinationDegrees = magneticDeclinationDegrees,
                    mode = mode,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.ForViewModel,
            initialValue = if (compass == null) State.SensorsMissing(detailsVisible = false)
            else State.SensorsPresent.Loading
        )

    private val compassModePrefValueMagneticNorth =
        application.getString(R.string.settings_compass_mode_magnetic_value)
    private val compassModePrefValueTrueNorth =
        application.getString(R.string.settings_compass_mode_true_value)
    private val prefsKeyCompassMode = application.getString(R.string.settings_compass_mode_key)
    private var prefCompassModeListener: PrefCompassModeListener? = null

    fun onSensorsMissingWhyClick() {
        sensorsMissingDetailsVisibleFlow.update { true }
    }

    sealed interface State {
        data class SensorsMissing(val detailsVisible: Boolean) : State
        sealed interface SensorsPresent : State {
            object Loading : SensorsPresent
            data class Loaded(
                val rotationMatrix: FloatArray,
                val accelerometerAccuracy: Compass.Accuracy?,
                val magnetometerAccuracy: Compass.Accuracy?,
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

    private inner class PrefCompassModeListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCompassMode) {
                producerScope.trySend(sharedPrefs.getString(key, null))
            }
        }
    }

    companion object {
        private val MAGNETIC_DECLINATION_LOCATION_THROTTLE_PERIOD = 5.minutes
    }
}