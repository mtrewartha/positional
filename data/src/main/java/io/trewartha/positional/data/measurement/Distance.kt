package io.trewartha.positional.data.measurement

sealed interface Distance {

    val value: Float

    fun inMeters(): Meters
    fun inFeet(): Feet

    @JvmInline
    value class Feet(override val value: Float) : Distance {
        override fun inFeet() = this
        override fun inMeters() = Meters(value * METERS_PER_FOOT)
    }

    @JvmInline
    value class Meters(override val value: Float) : Distance {
        override fun inFeet(): Feet = Feet(value * FEET_PER_METER)
        override fun inMeters(): Meters = this
    }
}

const val FEET_PER_METER = 3.28084f
const val METERS_PER_FOOT = 0.3048f
