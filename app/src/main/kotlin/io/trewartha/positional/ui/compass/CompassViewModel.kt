package io.trewartha.positional.ui.compass

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.hardware.*
import android.location.Location
import android.os.Looper
import android.view.Surface
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.*
import io.trewartha.positional.R
import io.trewartha.positional.compass.CompassMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CompassViewModel(app: Application) : AndroidViewModel(app) {

    val compassData: LiveData<CompassData>
        get() = _compassData

    private val _compassData = MediatorLiveData<CompassData>()

    private val sensorManager = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val locationClient = FusedLocationProviderClient(app)

    private val accelerometerAccuracy: LiveData<Int> = if (accelerometer == null) {
        emptyFlow<Int>()
    } else {
        callbackFlow {
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    sendBlocking(accuracy)
                }

                override fun onSensorChanged(event: SensorEvent) {
                    // Don't do anything
                }
            }
            sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY)
            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }.asLiveData()
    private val accelerometerReadings: LiveData<FloatArray> = if (accelerometer == null) {
        emptyFlow<FloatArray>()
    } else {
        callbackFlow {
            val listener = object : SensorEventListener {
                private val lastEventValues = floatArrayOf(0f, 0f, 0f)

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Don't do anything
                }

                override fun onSensorChanged(event: SensorEvent) {
                    lowPassFilter(event.values, lastEventValues)
                    sendBlocking(lastEventValues)
                }
            }
            sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY)
            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }.asLiveData()

    private val location = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.lastLocation ?: return
                Timber.d("Received location update")
                offer(location)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Timber.d("Location availability changed to $locationAvailability")
            }
        }

        try {
            val locationRequest = LocationRequest.create()
                    .setPriority(LOCATION_UPDATE_PRIORITY)
                    .setInterval(LOCATION_UPDATE_INTERVAL_MS)
            Timber.i("Requesting location updates: $locationRequest")
            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Timber.w(e, "Don't have location permissions, no location updates will be received")
        }

        awaitClose {
            Timber.i("Suspending location updates")
            locationClient.removeLocationUpdates(locationCallback)
        }
    }.asLiveData()

    private val magnetometerAccuracy: LiveData<Int> = if (magnetometer == null) {
        emptyFlow<Int>()
    } else {
        callbackFlow {
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    sendBlocking(accuracy)
                }

                override fun onSensorChanged(event: SensorEvent) {
                    // Don't do anything
                }
            }
            sensorManager.registerListener(listener, magnetometer, SENSOR_DELAY)
            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }.asLiveData()
    private val magnetometerReadings: LiveData<FloatArray> = if (magnetometer == null) {
        emptyFlow<FloatArray>()
    } else {
        callbackFlow {
            val listener = object : SensorEventListener {
                private val lastEventValues = floatArrayOf(0f, 0f, 0f)

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Don't do anything
                }

                override fun onSensorChanged(event: SensorEvent) {
                    lowPassFilter(event.values, lastEventValues)
                    sendBlocking(lastEventValues)
                }
            }
            sensorManager.registerListener(listener, magnetometer, SENSOR_DELAY)
            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }.asLiveData()

    private var mode: LiveData<CompassMode> = callbackFlow<String?> {
        if (prefs.contains(prefsKeyCompassMode))
            sendBlocking(prefs.getString(prefsKeyCompassMode, null))
        prefCompassModeListener = PrefCompassModeListener(this)
        prefs.registerOnSharedPreferenceChangeListener(prefCompassModeListener)
        awaitClose {
            prefCompassModeListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
        }
    }.map {
        CompassMode.valueOf(it!!.toUpperCase(Locale.US))
    }.catch {
        emit(DEFAULT_COMPASS_MODE)
    }.asLiveData()
    private val orientationData = FloatArray(3)
    private val prefs =
            app.getSharedPreferences(app.getString(R.string.settings_filename), Context.MODE_PRIVATE)
    private val prefsKeyCompassMode = app.getString(R.string.settings_compass_mode_key)
    private var prefCompassModeListener: PrefCompassModeListener? = null
    private val rotationMatrix = FloatArray(9)
    private val temporaryRotationMatrix = FloatArray(9)

    init {
        _compassData.apply {
            addSource(accelerometerAccuracy) {
                value = updateValue(
                        mode.value ?: return@addSource,
                        location.value ?: return@addSource,
                        it,
                        accelerometerReadings.value ?: return@addSource,
                        magnetometerAccuracy.value ?: return@addSource,
                        magnetometerReadings.value ?: return@addSource
                )
            }
            addSource(accelerometerReadings) {
                value = updateValue(
                        mode.value ?: return@addSource,
                        location.value ?: return@addSource,
                        accelerometerAccuracy.value ?: return@addSource,
                        it,
                        magnetometerAccuracy.value ?: return@addSource,
                        magnetometerReadings.value ?: return@addSource
                )
            }
            addSource(location) {
                value = updateValue(
                        mode.value ?: return@addSource,
                        it,
                        accelerometerAccuracy.value ?: return@addSource,
                        accelerometerReadings.value ?: return@addSource,
                        magnetometerAccuracy.value ?: return@addSource,
                        magnetometerReadings.value ?: return@addSource
                )
            }
            addSource(mode) {
                value = updateValue(
                        it,
                        location.value ?: return@addSource,
                        accelerometerAccuracy.value ?: return@addSource,
                        accelerometerReadings.value ?: return@addSource,
                        magnetometerAccuracy.value ?: return@addSource,
                        magnetometerReadings.value ?: return@addSource
                )
            }
            addSource(magnetometerAccuracy) {
                value = updateValue(
                        mode.value ?: return@addSource,
                        location.value ?: return@addSource,
                        accelerometerAccuracy.value ?: return@addSource,
                        accelerometerReadings.value ?: return@addSource,
                        it,
                        magnetometerReadings.value ?: return@addSource
                )
            }
            addSource(magnetometerReadings) {
                value = updateValue(
                        mode.value ?: return@addSource,
                        location.value ?: return@addSource,
                        accelerometerAccuracy.value ?: return@addSource,
                        accelerometerReadings.value ?: return@addSource,
                        magnetometerAccuracy.value ?: return@addSource,
                        it
                )
            }
        }
    }

    private fun adjustForDeviceOrientation() {
        when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> SensorManager.remapCoordinateSystem(
                    temporaryRotationMatrix,
                    SensorManager.AXIS_Z,
                    SensorManager.AXIS_Y,
                    rotationMatrix
            )
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                    temporaryRotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_Z,
                    rotationMatrix
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                    temporaryRotationMatrix,
                    SensorManager.AXIS_MINUS_Z,
                    SensorManager.AXIS_MINUS_Y,
                    rotationMatrix
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                    temporaryRotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_Z,
                    rotationMatrix
            )
        }
    }

    private fun lowPassFilter(input: FloatArray, output: FloatArray?): FloatArray? {
        if (output == null) return input
        for (i in input.indices) {
            output[i] = output[i] + LOW_PASS_FILTER_ALPHA * (input[i] - output[i])
        }
        return output
    }

    private fun updateValue(
            mode: CompassMode,
            location: Location,
            accelerometerAccuracy: Int,
            accelerometerReadings: FloatArray,
            magnetometerAccuracy: Int,
            magnetometerReadings: FloatArray
    ): CompassData {
        SensorManager.getRotationMatrix(
                temporaryRotationMatrix, null,
                accelerometerReadings, magnetometerReadings
        )
        adjustForDeviceOrientation()
        SensorManager.getOrientation(rotationMatrix, orientationData)

        var azimuth = (Math.toDegrees(orientationData[0].toDouble()).toFloat() + 360) % 360
        val declination = location.magneticDeclination
        if (mode == CompassMode.TRUE_NORTH)
            azimuth += declination

        return CompassData(azimuth, declination, mode, accelerometerAccuracy, magnetometerAccuracy)
    }

    private val Location.magneticDeclination: Float
        get() = GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitude.toFloat(),
                time
        ).declination

    data class CompassData(
            val azimuth: Float?,
            val declination: Float?,
            val mode: CompassMode?,
            val accelerometerAccuracy: Int?,
            val magnetometerAccuracy: Int?
    )

    private inner class PrefCompassModeListener(
            val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCompassMode)
                producerScope.offer(sharedPrefs.getString(key, null))
        }
    }

    companion object {
        private val DEFAULT_COMPASS_MODE = CompassMode.TRUE_NORTH
        private const val LOCATION_UPDATE_INTERVAL_MS = 300_000L // 5 minutes
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_LOW_POWER
        private const val LOW_PASS_FILTER_ALPHA = 0.95f
        private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI
    }
}