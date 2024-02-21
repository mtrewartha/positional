package io.trewartha.positional.data.compass

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.trewartha.positional.model.core.measurement.Angle
import kotlin.test.Test

class AzimuthTest {

    @Test
    fun `Constructor does not throw IllegalArgumentException when angle is between 0 and 360`() {
        shouldNotThrow<IllegalArgumentException> { Azimuth(Angle.Degrees(0f)) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is less than zero`() {
        shouldThrow<IllegalArgumentException> { Azimuth(Angle.Degrees(-0.0001f)) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is equal to 360`() {
        shouldThrow<IllegalArgumentException> { Azimuth(Angle.Degrees(360f)) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is greater than 360`() {
        shouldThrow<IllegalArgumentException> { Azimuth(Angle.Degrees(361f)) }
    }
}
