package io.trewartha.positional.core.measurement

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class AngleTest : DescribeSpec({

    describe("creating an angle from an integer") {
        it("creates an angle with the correct magnitude and unit") {
            checkAll(Arb.int()) { integer ->
                val degrees = integer.degrees
                degrees.shouldBe(Angle(integer.toDouble(), Angle.Unit.DEGREES))
            }
        }
    }

    describe("converting to degrees") {
        it("returns the original angle") {
            checkAll(Arb.int()) { integer ->
                val degrees = integer.degrees
                degrees.inDegrees().shouldBe(degrees)
            }
        }
    }

    describe("converting to a string") {
        context("with an arbitrary magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                checkAll(Arb.double()) { double ->
                    val result = double.degrees.toString()
                    result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
                }
            }
        }

        context("with a zero magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                val result = 0.0.degrees.toString()
                result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
            }
        }

        context("with a NaN magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                val result = Double.NaN.degrees.toString()
                result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
            }
        }

        context("with a positive infinite magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                val result = Double.POSITIVE_INFINITY.degrees.toString()
                result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
            }
        }

        context("with a negative infinite magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                val result = Double.NEGATIVE_INFINITY.degrees.toString()
                result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
            }
        }
    }

    describe("adding two angles") {
        it("sums the magnitudes in the same unit") {
            checkAll(Arb.double(), Arb.double()) { magnitude1, magnitude2 ->
                val result = magnitude1.degrees + magnitude2.degrees
                result.shouldBe((magnitude1 + magnitude2).degrees)
            }
        }
    }
})
