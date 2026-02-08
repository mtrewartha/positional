package io.trewartha.positional.compass

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.degrees

class AzimuthTest : AnnotationSpec() {

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

    @Test
    fun `Adding an angle to an azimuth sums the angles in the unit of the azimuth angle`() {
        val result = Azimuth(45.degrees) + (-5).degrees

        result.shouldBe(Azimuth(40.degrees))
    }

    @Test
    fun `Adding an angle to an azimuth wraps properly before 360 degrees`() {
        val result = Azimuth(1.degrees) + (-2).degrees

        result.shouldBe(Azimuth(359.degrees))
    }

    @Test
    fun `Adding an angle to an azimuth wraps properly after 360 degrees`() {
        val result = Azimuth(359.degrees) + 2.degrees

        result.shouldBe(Azimuth(1.degrees))
    }
}