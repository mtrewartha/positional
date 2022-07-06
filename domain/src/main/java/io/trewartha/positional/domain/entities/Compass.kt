package io.trewartha.positional.domain.entities

import kotlinx.coroutines.flow.Flow

interface Compass {

    enum class Accuracy { UNUSABLE, UNRELIABLE, LOW, MEDIUM, HIGH }

    val hasAccelerometer: Boolean
    val hasMagnetometer: Boolean

    val accelerometerAccuracyFlow: Flow<Accuracy?>
    val magnetometerAccuracyFlow: Flow<Accuracy?>

    val azimuthFlow: Flow<Float?>
    val magneticDeclinationFlow: Flow<Float>

    enum class Mode { MAGNETIC_NORTH, TRUE_NORTH }
}