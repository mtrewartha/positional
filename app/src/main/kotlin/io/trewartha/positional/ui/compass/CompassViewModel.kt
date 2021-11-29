package io.trewartha.positional.ui.compass

import android.app.Application
import android.content.SharedPreferences
import android.view.Surface
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.compass.Compass
import io.trewartha.positional.compass.CompassAccuracy
import io.trewartha.positional.compass.CompassMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CompassViewModel @Inject constructor(
    private val app: Application,
    private val compass: Compass,
    private val prefs: SharedPreferences,
    private val windowManager: WindowManager
) : AndroidViewModel(app) {

    val accelerometerAccuracy: LiveData<String> by lazy {
        compass.accelerometerAccuracy
            .map { getAccuracyText(it) }
            .asLiveData()
    }

    val azimuth: LiveData<String> by lazy {
        compass.azimuth
            .mapNotNull { it }
            .map {
                val azimuth = (adjustAzimuthForDisplayRotation(it).roundToInt() + 360) % 360
                app.getString(R.string.compass_degrees, azimuth)
            }
            .conflate()
            .onEach { Timber.d("Azimuth = $it") }
            .asLiveData()
    }

    val compassRotation: LiveData<Float> by lazy {
        compass.azimuth
            .mapNotNull { it }
            .map { -((adjustAzimuthForDisplayRotation(it) + 360f) % 360f) }
            .onStart { emit(0f) }
            .asLiveData()
    }

    val declination: LiveData<String> by lazy {
        (compass.magneticDeclination as Flow<Float?>)
            .onStart { emit(null) }
            .map {
                if (it == null) {
                    app.getString(R.string.common_dash)
                } else {
                    app.getString(R.string.compass_declination, it.roundToInt())
                }
            }
            .asLiveData()
    }

    val magnetometerAccuracy: LiveData<String> by lazy {
        compass.magnetometerAccuracy
            .map { getAccuracyText(it) }
            .asLiveData()
    }

    val missingSensorState: LiveData<MissingSensorState>
        get() = _missingSensorState

    val mode: LiveData<String> by lazy {
        callbackFlow {
            if (prefs.contains(prefsKeyCompassMode))
                trySend(prefs.getString(prefsKeyCompassMode, null))
            prefCompassModeListener = PrefCompassModeListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefCompassModeListener)
            awaitClose {
                prefCompassModeListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map {
            when (it) {
                compassModePrefValueMagneticNorth -> CompassMode.MAGNETIC_NORTH
                compassModePrefValueTrueNorth -> CompassMode.TRUE_NORTH
                else -> CompassMode.TRUE_NORTH
            }
        }.map {
            app.getString(
                when (it) {
                    CompassMode.MAGNETIC_NORTH -> R.string.compass_mode_magnetic_north
                    CompassMode.TRUE_NORTH -> R.string.compass_mode_true_north
                }
            )
        }.asLiveData()
    }

    private val _missingSensorState = MediatorLiveData<MissingSensorState>()
    private val compassModePrefValueMagneticNorth =
        app.getString(R.string.settings_compass_mode_magnetic_value)
    private val compassModePrefValueTrueNorth =
        app.getString(R.string.settings_compass_mode_true_value)
    private val prefsKeyCompassMode = app.getString(R.string.settings_compass_mode_key)
    private var prefCompassModeListener: PrefCompassModeListener? = null

    init {
        when {
            !compass.hasAccelerometer && !compass.hasMagnetometer -> {
                _missingSensorState.value = MissingSensorState(
                    app.getString(R.string.compass_missing_both_sensors_body),
                    app.getString(R.string.compass_missing_hardware_caption)
                )
            }
            !compass.hasAccelerometer -> {
                _missingSensorState.value = MissingSensorState(
                    app.getString(R.string.compass_missing_accelerometer_body),
                    app.getString(R.string.compass_missing_hardware_caption)
                )
            }
            !compass.hasMagnetometer -> {
                _missingSensorState.value = MissingSensorState(
                    app.getString(R.string.compass_missing_magnetometer_body),
                    app.getString(R.string.compass_missing_hardware_caption)
                )
            }
        }
    }

    private fun adjustAzimuthForDisplayRotation(azimuth: Float): Float {
        return when (val displayRotation = windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> azimuth
            Surface.ROTATION_90 -> azimuth - 270f
            Surface.ROTATION_180 -> azimuth - 180f
            Surface.ROTATION_270 -> azimuth - 90f
            else -> throw IllegalArgumentException("Unexpected display rotation: $displayRotation")
        }
    }

    private fun getAccuracyText(compassAccuracy: CompassAccuracy?): String {
        return app.getString(
            when (compassAccuracy) {
                CompassAccuracy.UNUSABLE -> R.string.compass_accuracy_no_contact
                CompassAccuracy.UNRELIABLE -> R.string.compass_accuracy_unreliable
                CompassAccuracy.LOW -> R.string.compass_accuracy_low
                CompassAccuracy.MEDIUM -> R.string.compass_accuracy_medium
                CompassAccuracy.HIGH -> R.string.compass_accuracy_high
                null -> R.string.compass_accuracy_unknown
            }
        )
    }

    data class MissingSensorState(val title: String, val body: String)

    private inner class PrefCompassModeListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCompassMode) {
                producerScope.trySend(sharedPrefs.getString(key, null))
            }
        }
    }
}