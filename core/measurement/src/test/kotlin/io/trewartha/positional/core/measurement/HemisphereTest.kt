package io.trewartha.positional.core.measurement

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HemisphereTest : DescribeSpec({

    describe("converting to a string") {
        context("when the hemisphere is north") {
            it("returns N") {
                Hemisphere.NORTH.toString().shouldBe("N")
            }
        }

        context("when the hemisphere is south") {
            it("returns S") {
                Hemisphere.SOUTH.toString().shouldBe("S")
            }
        }
    }
})
