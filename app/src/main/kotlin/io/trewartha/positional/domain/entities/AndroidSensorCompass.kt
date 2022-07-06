package io.trewartha.positional.domain.entities

import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.trewartha.positional.domain.utils.flow.throttleFirst
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AndroidSensorCompass @Inject constructor(
    private val locator: Locator,
    private val sensorManager: SensorManager
) : Compass {

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    override val hasAccelerometer: Boolean = accelerometer != null
    override val hasMagnetometer: Boolean = magnetometer != null

    override val accelerometerAccuracyFlow: Flow<Compass.Accuracy?>
        get() {
            if (!hasAccelerometer) {
                Timber.e("Unable to calculate accelerometer accuracy, device does not have accelerometer")
                return flowOf(null)
            }
            return callbackFlow {
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                        trySend(
                            when (accuracy) {
                                SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
                                    Compass.Accuracy.HIGH
                                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
                                    Compass.Accuracy.MEDIUM
                                SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
                                    Compass.Accuracy.LOW
                                SensorManager.SENSOR_STATUS_UNRELIABLE ->
                                    Compass.Accuracy.UNRELIABLE
                                else ->
                                    Compass.Accuracy.UNUSABLE
                            }
                        )
                    }

                    override fun onSensorChanged(event: SensorEvent) {
                        // Don't do anything
                    }
                }
                sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY)
                awaitClose { sensorManager.unregisterListener(listener) }
            }.conflate()
        }

    override val magnetometerAccuracyFlow: Flow<Compass.Accuracy?>
        get() {
            if (!hasMagnetometer) {
                Timber.e("Unable to calculate magnetometer accuracy, device does not have magnetometer")
                return MutableStateFlow(null)
            }
            return callbackFlow {
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                        trySend(
                            when (accuracy) {
                                SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
                                    Compass.Accuracy.HIGH
                                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
                                    Compass.Accuracy.MEDIUM
                                SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
                                    Compass.Accuracy.LOW
                                SensorManager.SENSOR_STATUS_UNRELIABLE ->
                                    Compass.Accuracy.UNRELIABLE
                                else ->
                                    Compass.Accuracy.UNUSABLE
                            }
                        )
                    }

                    override fun onSensorChanged(event: SensorEvent) {
                        // Don't do anything
                    }
                }
                sensorManager.registerListener(listener, magnetometer, SENSOR_DELAY)
                awaitClose { sensorManager.unregisterListener(listener) }
            }.conflate()
        }

    override val azimuthFlow: Flow<Float?>
        get() {
            if (!hasAccelerometer || !hasMagnetometer) {
                Timber.e("Unable to calculate azimuth, device has accelerometer = $hasAccelerometer, magnetometer = $hasMagnetometer")
                return MutableStateFlow(null)
            }
            return callbackFlow {
                val accelerometerReadings = FloatArray(3)
                val magnetometerReadings = FloatArray(3)
                val rotation = FloatArray(9)
                val inclination = FloatArray(9)
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        // Don't do anything
                    }

                    override fun onSensorChanged(event: SensorEvent) {
                        when (event.sensor) {
                            accelerometer -> smoothAndSetReadings(
                                accelerometerReadings,
                                event.values
                            )
                            magnetometer -> smoothAndSetReadings(magnetometerReadings, event.values)
                        }
                        val successfullyCalculatedRotationMatrix = SensorManager.getRotationMatrix(
                            rotation,
                            inclination,
                            accelerometerReadings,
                            magnetometerReadings
                        )
                        if (successfullyCalculatedRotationMatrix) {
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(rotation, orientation)
                            val currentAzimuth =
                                ((orientation[0] + TWO_PI) % TWO_PI) * DEGREES_PER_RADIAN
                            trySend(currentAzimuth.toFloat())
                        }
                    }
                }
                sensorManager.registerListener(listener, accelerometer, SENSOR_DELAY)
                sensorManager.registerListener(listener, magnetometer, SENSOR_DELAY)
                awaitClose { sensorManager.unregisterListener(listener) }
            }.conflate()
        }

    override val magneticDeclinationFlow: Flow<Float>
        get() = locator.locationFlow
            .throttleFirst(LOCATION_THROTTLE_PERIOD_MS)
            .map { location ->
                GeomagneticField(
                    location.latitude.toFloat(),
                    location.longitude.toFloat(),
                    location.altitudeMeters?.toFloat() ?: 0f,
                    location.timestamp.toEpochMilliseconds()
                ).declination
            }.distinctUntilChanged()

    private fun smoothAndSetReadings(readings: FloatArray, newReadings: FloatArray) {
        readings[0] = READINGS_ALPHA * newReadings[0] + (1 - READINGS_ALPHA) * readings[0]
        readings[1] = READINGS_ALPHA * newReadings[1] + (1 - READINGS_ALPHA) * readings[1]
        readings[2] = READINGS_ALPHA * newReadings[2] + (1 - READINGS_ALPHA) * readings[2]
    }

    companion object {
        private const val DEGREES_PER_RADIAN = 180 / Math.PI
        private const val LOCATION_THROTTLE_PERIOD_MS = 300_000L // 5 minutes
        private const val READINGS_ALPHA = 0.06f
        private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME
        private const val TWO_PI = 2.0 * Math.PI
    }
}