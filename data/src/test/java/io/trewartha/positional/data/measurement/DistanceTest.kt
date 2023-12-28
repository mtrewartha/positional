package io.trewartha.positional.data.measurement

import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class DistanceTest {

    @Test
    fun testConversionFromFeetToFeet() {
        val feet = Distance.Feet(1f)

        val result = feet.inFeet()

        result.shouldBeInstanceOf<Distance.Feet>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun testConversionFromFeetToMeters() {
        val feet = Distance.Feet(1f)

        val result = feet.inMeters()

        result.shouldBeInstanceOf<Distance.Meters>()
        result.value.shouldBeExactly(0.3048f)
    }

    @Test
    fun testConversionFromMetersToFeet() {
        val meters = Distance.Meters(1f)

        val result = meters.inFeet()

        result.shouldBeInstanceOf<Distance.Feet>()
        result.value.shouldBeExactly(3.28084f)
    }

    @Test
    fun testConversionFromMetersToMeters() {
        val meters = Distance.Meters(1f)

        val result = meters.inMeters()

        result.shouldBeInstanceOf<Distance.Meters>()
        result.value.shouldBeExactly(1f)
    }
}
