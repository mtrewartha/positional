package io.trewartha.positional.model.core.measurement

/**
 * Angle abstraction
 */
sealed interface Angle {

    /**
     * Converts this angle to degrees
     */
    fun inDegrees(): Degrees

    /**
     * Adds another angle to this angle
     */
    operator fun plus(other: Angle): Angle

    /**
     * Angle in degrees
     *
     * @property value The magnitude of the angle
     */
    @JvmInline
    value class Degrees @Throws(IllegalArgumentException::class) constructor(
        val value: Double
    ) : Angle {

        init {
            require(value.isFinite()) { "Value '$value' is not finite" }
        }

        override fun inDegrees(): Degrees = this

        override operator fun plus(other: Angle): Angle =
            Degrees((value + other.inDegrees().value + DEGREES_360) % DEGREES_360)

        override fun toString(): String = "${value}°"
    }
}

/**
 * Create an angle in degrees with the magnitude of this value
 */
val Number.degrees: Angle.Degrees get() = Angle.Degrees(toDouble())

private const val DEGREES_360 = 360.0
