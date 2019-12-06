package io.trewartha.positional

import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SampleSpec: Spek({
    describe("Spek framework") {
        it("runs tests successfully") {
            assertThat(true).isTrue()
        }
    }
})