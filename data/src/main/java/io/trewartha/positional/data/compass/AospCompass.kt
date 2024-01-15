package io.trewartha.positional.data.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.trewartha.positional.data.measurement.Angle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [Compass] implementation powered by the AOSP [SensorManager]
 */
class AospCompass @Inject constructor(
    coroutineContext: CoroutineContext,
    private val sensorManager: SensorManager,
    accelerometer: Sensor,
    magnetometer: Sensor,
    private val rotationVectorSensor: Sensor
) : Compass {

    private val orientation = FloatArray(ROTATION_VECTOR_SIZE)

    private val accelerometerAccuracy: Flow<CompassAccuracy?> =
        sensorManager.getAccuracyFlow(accelerometer)

    private val magnetometerAccuracy: Flow<CompassAccuracy?> =
        sensorManager.getAccuracyFlow(magnetometer)

    private val rotation: Flow<FloatArray> =
        callbackFlow {
            val rotationVector = FloatArray(ROTATION_VECTOR_SIZE)
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Don't do anything
                }

                override fun onSensorChanged(event: SensorEvent) {
                    System.arraycopy(event.values, 0, rotationVector, 0, rotationVector.size)
                    val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
                    trySend(rotationMatrix)
                }
            }
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SAMPLING_PERIOD_US,
                MAX_REPORT_LATENCY_US
            )
            awaitClose { sensorManager.unregisterListener(listener) }
        }.conflate()

    override val azimuth: Flow<Azimuth> =
        combine(
            rotation,
            accelerometerAccuracy,
            magnetometerAccuracy
        ) { rotation, accelerometerAccuracy, magnetometerAccuracy ->
            try {
                SensorManager.getOrientation(rotation, orientation)
                val angle = Angle.Degrees(
                    (Math.toDegrees(orientation[0].toDouble())
                        .toFloat() + DEGREES_360) % DEGREES_360
                )
                Azimuth(angle, accelerometerAccuracy, magnetometerAccuracy)
            } catch (_: Exception) {
                null
            }
        }.filterNotNull().flowOn(coroutineContext)
}

private fun SensorManager.getAccuracyFlow(sensor: Sensor): Flow<CompassAccuracy?> =
    callbackFlow {
        // Some devices don't seem to trigger the listener below, so send a null accuracy
        // indicating we don't have one right away. If we later get an accuracy, it'll be sent.
        trySend(null)
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
        registerListener(listener, sensor, SAMPLING_PERIOD_US)
        awaitClose { unregisterListener(listener) }
    }.conflate()

private const val DEGREES_360 = 360f
private const val MAX_REPORT_LATENCY_US = SensorManager.SENSOR_DELAY_UI
private const val ROTATION_MATRIX_SIZE = 9
private const val ROTATION_VECTOR_SIZE = 3
private const val SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME
