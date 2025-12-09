package io.trewartha.positional.core.measurement

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HemisphereTest {

    @Test
    fun `Given NORTH hemisphere, when converting to string, then returns N`() {
        // Given
        val hemisphere = Hemisphere.NORTH

        // When
        val result = hemisphere.toString()

        // Then
        result.shouldBe("N")
    }

    @Test
    fun `Given SOUTH hemisphere, when converting to string, then returns S`() {
        // Given
        val hemisphere = Hemisphere.SOUTH

        // When
        val result = hemisphere.toString()

        // Then
        result.shouldBe("S")
    }
}
