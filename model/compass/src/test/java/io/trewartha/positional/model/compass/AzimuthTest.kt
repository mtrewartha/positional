package io.trewartha.positional.model.compass

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.trewartha.positional.model.core.measurement.degrees
import kotlin.test.Test

class AzimuthTest {

    @Test
    fun `Constructor does not throw IllegalArgumentException when angle is between 0 and 360`() {
        shouldNotThrow<IllegalArgumentException> { Azimuth(0.degrees) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is less than zero`() {
        shouldThrow<IllegalArgumentException> { Azimuth((-0.0001).degrees) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is equal to 360`() {
        shouldThrow<IllegalArgumentException> { Azimuth(360.degrees) }
    }

    @Test
    fun `Constructor throws IllegalArgumentException when angle is greater than 360`() {
        shouldThrow<IllegalArgumentException> { Azimuth(361.degrees) }
    }
}
