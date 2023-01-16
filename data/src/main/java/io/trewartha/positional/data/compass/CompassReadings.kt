package io.trewartha.positional.data.compass

data class CompassReadings(
    val rotationMatrix: FloatArray,
    val accelerometerAccuracy: CompassAccuracy?,
    val magnetometerAccuracy: CompassAccuracy?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompassReadings

        if (!rotationMatrix.contentEquals(other.rotationMatrix)) return false
        if (accelerometerAccuracy != other.accelerometerAccuracy) return false
        if (magnetometerAccuracy != other.magnetometerAccuracy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rotationMatrix.contentHashCode()
        result = 31 * result + (accelerometerAccuracy?.hashCode() ?: 0)
        result = 31 * result + (magnetometerAccuracy?.hashCode() ?: 0)
        return result
    }
}
