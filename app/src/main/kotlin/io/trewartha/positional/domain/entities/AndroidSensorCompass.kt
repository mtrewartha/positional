package io.trewartha.positional.domain.entities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOf

class AndroidSensorCompass @Inject constructor(
    private val sensorManager: SensorManager,
    private val rotationVectorSensor: Sensor,
    private val accelerometer: Sensor?,
    private val magnetometer: Sensor?,
) : Compass {

    override val accelerometerAccuracyFlow: Flow<Compass.Accuracy?>
        get() = accelerometer?.let { sensorManager.getAccuracyFlow(it) } ?: flowOf(null)

    override val magnetometerAccuracyFlow: Flow<Compass.Accuracy?>
        get() = magnetometer?.let { sensorManager.getAccuracyFlow(it) } ?: flowOf(null)

    override val rotationMatrixFlow: Flow<FloatArray>
        get() {
            return callbackFlow {
                val rotationVector = FloatArray(3)
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        // Don't do anything
                    }

                    override fun onSensorChanged(event: SensorEvent) {
                        System.arraycopy(event.values, 0, rotationVector, 0, rotationVector.size)
                        val rotationMatrix = FloatArray(9)
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
        }

    private fun SensorManager.getAccuracyFlow(sensor: Sensor): Flow<Compass.Accuracy> {
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
            registerListener(listener, sensor, SAMPLING_PERIOD_US)
            awaitClose { unregisterListener(listener) }
        }.conflate()
    }

    companion object {
        private const val MAX_REPORT_LATENCY_US = SensorManager.SENSOR_DELAY_UI
        private const val SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME
    }
}