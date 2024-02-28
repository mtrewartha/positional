package io.trewartha.positional.model.core.measurement

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

    override operator fun plus(other: Angle): Angle = (value + other.inDegrees().value).degrees

    override fun toString(): String = "${value}°"
}

/**
 * Create an angle in degrees with the magnitude of this value
 */
val Number.degrees: Degrees get() = Degrees(toDouble())
