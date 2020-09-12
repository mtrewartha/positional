package io.trewartha.positional.ui.compass

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.Surface
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import io.trewartha.positional.PositionalApplication
import io.trewartha.positional.R
import io.trewartha.positional.compass.Compass
import io.trewartha.positional.compass.CompassAccuracy
import io.trewartha.positional.compass.CompassMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.roundToInt

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CompassViewModel(app: Application) : AndroidViewModel(app) {

    val missingSensors: LiveData<Data.MissingSensor>
        get() = _missingSensors
    val readings: LiveData<Data.Readings>
        get() = _readings

    private val _missingSensors = MediatorLiveData<Data.MissingSensor>()
    private val _readings: LiveData<Data.Readings> by lazy {
        combine(
                compass.azimuth,
                compass.magneticDeclination,
                mode,
                compass.accelerometerAccuracy,
                compass.magnetometerAccuracy,
        ) { azimuth, declination, mode, accelerometerAccuracy, magnetometerAccuracy ->
            val azimuthInt = (adjustAzimuthForDisplayRotation(azimuth).roundToInt() + 360) % 360
            val azimuthFloat = (adjustAzimuthForDisplayRotation(azimuth) + 360f) % 360f
            Data.Readings(
                    azimuthInt,
                    -azimuthFloat,
                    declination.roundToInt(),
                    mode,
                    accelerometerAccuracy,
                    magnetometerAccuracy
            )
        }.conflate().asLiveData()
    }
    private val compass = Compass(app)
    private var mode: Flow<CompassMode> = callbackFlow {
        if (prefs.contains(prefsKeyCompassMode))
            offer(prefs.getString(prefsKeyCompassMode, null))
        prefCompassModeListener = PrefCompassModeListener(this)
        prefs.registerOnSharedPreferenceChangeListener(prefCompassModeListener)
        awaitClose {
            prefCompassModeListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
        }
    }.map {
        try {
            CompassMode.valueOf(it!!.toUpperCase(Locale.US))
        } catch (exception: Exception) {
            CompassMode.TRUE_NORTH
        }
    }
    private val prefs = app.getSharedPreferences(
            app.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
    )
    private val prefsKeyCompassMode = app.getString(R.string.settings_compass_mode_key)
    private var prefCompassModeListener: PrefCompassModeListener? = null
    private val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    init {
        val context by lazy { getApplication<PositionalApplication>() }
        when {
            !compass.hasAccelerometer && !compass.hasMagnetometer -> {
                _missingSensors.value = Data.MissingSensor(
                        context.getString(R.string.compass_missing_both_sensors_body),
                        context.getString(R.string.compass_missing_both_sensors_caption)
                )
            }
            !compass.hasAccelerometer -> {
                _missingSensors.value = Data.MissingSensor(
                        context.getString(R.string.compass_missing_accelerometer_body),
                        context.getString(R.string.compass_missing_accelerometer_caption)
                )
            }
            !compass.hasMagnetometer -> {
                _missingSensors.value = Data.MissingSensor(
                        context.getString(R.string.compass_missing_magnetometer_body),
                        context.getString(R.string.compass_missing_magnetometer_caption)
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

    sealed class Data {
        data class Readings(
                val azimuth: Int,
                val compassViewRotation: Float,
                val declination: Int,
                val mode: CompassMode,
                val accelerometerAccuracy: CompassAccuracy,
                val magnetometerAccuracy: CompassAccuracy
        ) : Data()

        data class MissingSensor(val bodyText: String, val captionText: String) : Data()
    }

    private inner class PrefCompassModeListener(
            val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCompassMode) {
                producerScope.offer(sharedPrefs.getString(key, null))
            }
        }
    }
}