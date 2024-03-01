package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HemisphereTest {

    @Test
    fun `Conversion to string returns first letter`() {
        for (hemisphere in Hemisphere.entries) {
            hemisphere.toString().shouldBe(hemisphere.name.first().uppercase())
        }
    }
}
