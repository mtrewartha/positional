package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class DistanceTest {

    @Test
    fun `Conversion from feet to feet returns original feet`() {
        val feet = Distance.Feet(1f)

        val result = feet.inFeet()

        result.shouldBeInstanceOf<Distance.Feet>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun `Conversion from feet to meters returns correct distance in meters`() {
        val feet = Distance.Feet(1f)

        val result = feet.inMeters()

        result.shouldBeInstanceOf<Distance.Meters>()
        result.value.shouldBeExactly(0.3048f)
    }

    @Test
    fun `Conversion from meters to feet returns correct distance in feet`() {
        val meters = Distance.Meters(1f)

        val result = meters.inFeet()

        result.shouldBeInstanceOf<Distance.Feet>()
        result.value.shouldBeExactly(3.28084f)
    }

    @Test
    fun `Conversion from meters to meters returns original meters`() {
        val meters = Distance.Meters(1f)

        val result = meters.inMeters()

        result.shouldBeInstanceOf<Distance.Meters>()
        result.value.shouldBeExactly(1f)
    }
}
