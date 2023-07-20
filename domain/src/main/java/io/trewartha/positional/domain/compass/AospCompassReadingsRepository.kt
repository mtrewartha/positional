package io.trewartha.positional.domain.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassReadings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class AospCompassReadingsRepository @Inject constructor(
    private val sensorManager: SensorManager,
) : CompassReadingsRepository {

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val accelerometerAccuracyFlow: Flow<CompassAccuracy> =
        sensorManager.getAccuracyFlow(accelerometer)

    private val magnetometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val magnetometerAccuracyFlow: Flow<CompassAccuracy> =
        sensorManager.getAccuracyFlow(magnetometer)

    private val rotationMatrixFlow: Flow<FloatArray> =
        callbackFlow {
            if (rotationVectorSensor == null) throw CompassHardwareException()
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

    private val rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    override val compassReadingsFlow: Flow<CompassReadings> = combine(
        rotationMatrixFlow,
        accelerometerAccuracyFlow,
        magnetometerAccuracyFlow
    ) { rotationMatrix, accelerometerAccuracy, magnetometerAccuracy ->
        CompassReadings(rotationMatrix, accelerometerAccuracy, magnetometerAccuracy)
    }

    private fun SensorManager.getAccuracyFlow(sensor: Sensor?): Flow<CompassAccuracy> {
        return callbackFlow {
            if (sensor == null) throw CompassHardwareException()
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
    }

    companion object {
        private const val MAX_REPORT_LATENCY_US = SensorManager.SENSOR_DELAY_UI
        private const val ROTATION_MATRIX_SIZE = 9
        private const val ROTATION_VECTOR_SIZE = 3
        private const val SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME
    }
}
