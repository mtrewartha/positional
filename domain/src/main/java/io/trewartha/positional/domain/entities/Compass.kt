package io.trewartha.positional.domain.entities

import kotlinx.coroutines.flow.Flow

interface Compass {

    enum class Accuracy { UNUSABLE, UNRELIABLE, LOW, MEDIUM, HIGH }

    /**
     * A [Flow] that emits the [Accuracy] of the accelerometer over time or `null` if no
     * accelerometer is present
     */
    val accelerometerAccuracyFlow: Flow<Accuracy?>

    /**
     * A [Flow] that emits the [Accuracy] of the magnetometer over time or `null` if no magnetometer
     * is in use
     */
    val magnetometerAccuracyFlow: Flow<Accuracy?>

    /**
     * TODO: A [Flow] that emits a [FloatArray]...
     */
    val rotationMatrixFlow: Flow<FloatArray>
}