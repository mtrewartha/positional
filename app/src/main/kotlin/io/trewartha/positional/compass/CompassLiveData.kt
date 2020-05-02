package io.trewartha.positional.compass

import android.content.Context
import android.content.SharedPreferences
import android.hardware.*
import android.location.Location
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import androidx.lifecycle.MediatorLiveData
import io.trewartha.positional.R
import io.trewartha.positional.location.LocationLiveData


class CompassLiveData(context: Context) : MediatorLiveData<CompassViewData>() {

    private val sensorManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        context.getSystemService(SensorManager::class.java)
    else
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val windowManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        context.getSystemService(WindowManager::class.java)
    else
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val accelerometerListener = AccelerometerSensorListener()
    private val accelerometerReadings = FloatArray(3)
    private val compassModeListener = CompassModeListener()
    private val locationLiveData = LocationLiveData(context)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val magnetometerListener = MagnetometerSensorListener()
    private val magnetometerReadings = FloatArray(3)
    private val orientationData = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val sharedPrefs = context
            .getSharedPreferences(context.getString(R.string.settings_filename), Context.MODE_PRIVATE)
    private val sharedPrefCompassMode = context
            .getString(R.string.settings_compass_mode_key)
    private val sharedPrefCompassModeTrue = context
            .getString(R.string.settings_compass_mode_true_value)
    private val sharedPrefCompassModeMagnetic = context
            .getString(R.string.settings_compass_mode_magnetic_value)
    private val temporaryRotationMatrix = FloatArray(9)

    private var azimuth: Float? = null
    private var declination: Float? = null
    private var location: Location? = null
    private var mode: CompassMode? = null
    private var accelerometerAccuracy: Int? = null
    private var magnetometerAccuracy: Int? = null

    override fun onActive() {
        super.onActive()

        addSource(locationLiveData) {
            location = it
            updateValue()
        }

        if (accelerometer != null && magnetometer != null)
            sensorManager.apply {
                registerListener(accelerometerListener, accelerometer, SENSOR_DELAY)
                registerListener(magnetometerListener, magnetometer, SENSOR_DELAY)
            }
        sharedPrefs.registerOnSharedPreferenceChangeListener(compassModeListener)

        mode = getCompassModePreference()
    }

    override fun onInactive() {
        super.onInactive()

        removeSource(locationLiveData)

        if (accelerometer != null && magnetometer != null)
            sensorManager.apply {
                unregisterListener(accelerometerListener)
                unregisterListener(magnetometerListener)
            }
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(compassModeListener)
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

    private fun getCompassModePreference(): CompassMode {
        val magneticNorthPreference = sharedPrefs.getString(
                sharedPrefCompassMode,
                sharedPrefCompassModeTrue
        ) == sharedPrefCompassModeMagnetic

        return if (magneticNorthPreference) {
            CompassMode.MAGNETIC_NORTH
        } else {
            CompassMode.TRUE_NORTH
        }
    }

    private fun getDeclination(location: Location?): Float? = location?.let {
        GeomagneticField(
                it.latitude.toFloat(),
                it.longitude.toFloat(),
                it.altitude.toFloat(),
                it.time
        ).declination
    }

    private fun updateValue() {
        SensorManager.getRotationMatrix(
                temporaryRotationMatrix, null,
                accelerometerReadings, magnetometerReadings
        )
        adjustForDeviceOrientation()
        SensorManager.getOrientation(rotationMatrix, orientationData)
        azimuth = (Math.toDegrees(orientationData[0].toDouble()).toFloat() + 360) % 360
        declination = getDeclination(location)

        if (mode == CompassMode.TRUE_NORTH)
            azimuth = azimuth?.let { a -> declination?.let { d -> a + d } }

        value = CompassViewData(
                azimuth, declination, mode,
                accelerometerAccuracy, magnetometerAccuracy
        )
    }

    private inner class CompassModeListener : SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == sharedPrefCompassMode) mode = getCompassModePreference()
        }
    }

    private inner class AccelerometerSensorListener : SensorEventListener {

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            accelerometerAccuracy = accuracy
            updateValue()
        }

        override fun onSensorChanged(event: SensorEvent) {
            accelerometerReadings[0] = ALPHA * accelerometerReadings[0] + (1 - ALPHA) *
                    event.values[0]
            accelerometerReadings[1] = ALPHA * accelerometerReadings[1] + (1 - ALPHA) *
                    event.values[1]
            accelerometerReadings[2] = ALPHA * accelerometerReadings[2] + (1 - ALPHA) *
                    event.values[2]
            updateValue()
        }
    }

    private inner class MagnetometerSensorListener : SensorEventListener {

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            magnetometerAccuracy = accuracy
            updateValue()
        }

        override fun onSensorChanged(event: SensorEvent) {
            magnetometerReadings[0] = ALPHA * magnetometerReadings[0] + (1 - ALPHA) *
                    event.values[0]
            magnetometerReadings[1] = ALPHA * magnetometerReadings[1] + (1 - ALPHA) *
                    event.values[1]
            magnetometerReadings[2] = ALPHA * magnetometerReadings[2] + (1 - ALPHA) *
                    event.values[2]
            updateValue()
        }
    }

    companion object {
        private const val ALPHA = 0.95f
        private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI
    }
}