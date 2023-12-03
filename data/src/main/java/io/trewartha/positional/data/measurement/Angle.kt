package io.trewartha.positional.data.measurement

sealed interface Angle {

    fun inDegrees(): Degrees

    operator fun plus(other: Angle): Angle

    @JvmInline
    value class Degrees(val value: Float) : Angle {
        override fun inDegrees(): Degrees = this

        override operator fun plus(other: Angle): Angle = Degrees(value + other.inDegrees().value)
    }
}
