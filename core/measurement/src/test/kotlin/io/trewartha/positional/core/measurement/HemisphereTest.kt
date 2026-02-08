package io.trewartha.positional.core.measurement

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class HemisphereTest : AnnotationSpec() {

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
