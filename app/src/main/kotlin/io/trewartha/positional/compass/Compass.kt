package io.trewartha.positional.compass

import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class Compass @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sensorManager: SensorManager
) {

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val accelerometerAccuracy: Flow<CompassAccuracy?>
        get() {
            if (!hasAccelerometer) {
                Timber.e("Unable to calculate accelerometer accuracy, device does not have accelerometer")
                return flowOf(null)
            }
            return callbackFlow<CompassAccuracy> {
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                        trySend(
                            when (accuracy) {
                                SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
                                    CompassAccuracy.HIGH
                                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
                                    CompassAccuracy.MEDIUM
                                SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
                                    CompassAccuracy.LOW
                                SensorManager.SENSOR_STATUS_UNRELIABLE ->
                                    CompassAccuracy.UNRELIABLE
                                else ->
                                    CompassAccuracy.UNUSABLE
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

    val azimuth: Flow<Float?>
        get() {
            if (!hasAccelerometer || !hasMagnetometer) {
                Timber.e("Unable to calculate azimuth, device has accelerometer = $hasAccelerometer, magnetometer = $hasMagnetometer")
                return MutableStateFlow(null)
            }
            return callbackFlow<Float> {
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

    val hasAccelerometer: Boolean
        get() = accelerometer != null

    val hasMagnetometer: Boolean
        get() = magnetometer != null

    val magneticDeclination: Flow<Float>
        get() = callbackFlow {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Timber.d("Received location update")
                    val declination = with(locationResult.lastLocation) {
                        GeomagneticField(
                            latitude.toFloat(),
                            longitude.toFloat(),
                            altitude.toFloat(),
                            time
                        )
                    }.declination
                    trySend(declination)
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
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Timber.w(e, "Don't have location permissions, no location updates will be received")
            }

            awaitClose {
                Timber.i("Suspending location updates")
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }.distinctUntilChanged()

    val magnetometerAccuracy: Flow<CompassAccuracy?>
        get() {
            if (!hasMagnetometer) {
                Timber.e("Unable to calculate magnetometer accuracy, device does not have magnetometer")
                return MutableStateFlow(null)
            }
            return callbackFlow<CompassAccuracy> {
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                        trySend(
                            when (accuracy) {
                                SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
                                    CompassAccuracy.HIGH
                                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
                                    CompassAccuracy.MEDIUM
                                SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
                                    CompassAccuracy.LOW
                                SensorManager.SENSOR_STATUS_UNRELIABLE ->
                                    CompassAccuracy.UNRELIABLE
                                else ->
                                    CompassAccuracy.UNUSABLE
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

    private fun smoothAndSetReadings(readings: FloatArray, newReadings: FloatArray) {
        readings[0] = READINGS_ALPHA * newReadings[0] + (1 - READINGS_ALPHA) * readings[0]
        readings[1] = READINGS_ALPHA * newReadings[1] + (1 - READINGS_ALPHA) * readings[1]
        readings[2] = READINGS_ALPHA * newReadings[2] + (1 - READINGS_ALPHA) * readings[2]
    }

    companion object {
        private const val DEGREES_PER_RADIAN = 180 / Math.PI
        private const val LOCATION_UPDATE_INTERVAL_MS = 300_000L // 5 minutes
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_LOW_POWER
        private const val READINGS_ALPHA = 0.06f
        private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME
        private const val TWO_PI = 2.0 * Math.PI
    }
}