package io.trewartha.positional.data.measurement

sealed interface Angle {

    fun inDegrees(): Degrees

    operator fun plus(other: Angle): Angle

    @JvmInline
    value class Degrees @Throws(IllegalArgumentException::class) constructor(
        val value: Float
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

private const val DEGREES_360 = 360f
