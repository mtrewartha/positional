package io.trewartha.positional.data.measurement

sealed interface Angle {

    fun inDegrees(): Degrees

    @JvmInline
    value class Degrees(val value: Float) : Angle {
        override fun inDegrees(): Degrees = this
    }
}
