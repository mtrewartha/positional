package io.trewartha.positional.core.measurement

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlin.test.Test

class DistanceTest {

    @Test
    fun `Distances in feet can be created with extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.feet.shouldBe(Distance(number.toDouble(), Distance.Unit.FEET))
        }
    }

    @Test
    fun `Distances in meters can be created with extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.meters.shouldBe(Distance(number.toDouble(), Distance.Unit.METERS))
        }
    }

    @Test
    fun `Distance is negative if magnitude is less than zero`() {
        for (unit in Distance.Unit.entries) {
            for (negativeMagnitude in listOf(-2.0, -1.0)) {
                Distance(negativeMagnitude, unit).isNegative.shouldBeTrue()
            }
            Distance(0.0, unit).isNegative.shouldBeFalse()
            for (positiveMagnitude in listOf(1.0, 2.0)) {
                Distance(positiveMagnitude, unit).isNegative.shouldBeFalse()
            }
        }
    }

    @Test
    fun `Distance is positive if magnitude is greater than zero`() {
        for (unit in Distance.Unit.entries) {
            for (negativeMagnitude in listOf(-2.0, -1.0)) {
                Distance(negativeMagnitude, unit).isPositive.shouldBeFalse()
            }
            Distance(0.0, unit).isPositive.shouldBeFalse()
            for (positiveMagnitude in listOf(1.0, 2.0)) {
                Distance(positiveMagnitude, unit).isPositive.shouldBeTrue()
            }
        }
    }

    @Test
    fun `Distance is finite if magnitude is neither positive nor negative infinity`() {
        for (unit in Distance.Unit.entries) {
            for (infiniteMagnitude in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)) {
                Distance(infiniteMagnitude, unit).isFinite.shouldBeFalse()
            }
            for (finiteMagnitude in listOf(-2.0, -1.0, 0.0, 1.0, 2.0)) {
                Distance(finiteMagnitude, unit).isFinite.shouldBeTrue()
            }
        }
    }

    @Test
    fun `Conversion from feet to feet returns original feet`() {
        val feet = 1.feet

        val result = feet.inFeet()

        result.shouldBe(Distance(1.0, Distance.Unit.FEET))
    }

    @Test
    fun `Conversion from feet to meters returns correct distance in meters`() {
        val feet = 1.feet

        val result = feet.inMeters()

        result.shouldBe(Distance(0.3048, Distance.Unit.METERS))
    }

    @Test
    fun `Conversion from feet to a string uses standard unit name`() {
        val feet = 1.23.feet

        val result = feet.toString()

        result.shouldEndWith(" ft")
    }

    @Test
    fun `Conversion from meters to feet returns correct distance in feet`() {
        val meters = 1.meters

        val result = meters.inFeet()

        result.shouldBe(Distance(3.28084, Distance.Unit.FEET))
    }

    @Test
    fun `Conversion from meters to meters returns original meters`() {
        val meters = 1.meters

        val result = meters.inMeters()

        result.shouldBe(Distance(1.0, Distance.Unit.METERS))
    }

    @Test
    fun `Conversion from meters to a string uses standard unit name`() {
        val meters = 1.23.meters

        val result = meters.toString()

        result.shouldEndWith(" m")
    }
}
