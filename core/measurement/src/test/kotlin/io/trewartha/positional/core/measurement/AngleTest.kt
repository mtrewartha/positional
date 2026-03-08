package io.trewartha.positional.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.checkAll

class AngleTest : DescribeSpec({

    describe("creating an angle") {
        context("with a finite magnitude") {
            it("creates an angle with the given magnitude and unit") {
                checkAll(Arb.numericDouble()) { magnitude ->
                    val angle = Angle(magnitude, Angle.Unit.DEGREES)
                    angle.magnitude.shouldBe(magnitude)
                    angle.unit.shouldBe(Angle.Unit.DEGREES)
                }
            }
        }

        context("with a NaN magnitude") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { Angle(Double.NaN, Angle.Unit.DEGREES) }
            }
        }

        context("with a positive infinite magnitude") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    Angle(Double.POSITIVE_INFINITY, Angle.Unit.DEGREES)
                }
            }
        }

        context("with a negative infinite magnitude") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    Angle(Double.NEGATIVE_INFINITY, Angle.Unit.DEGREES)
                }
            }
        }
    }

    describe("converting to degrees") {
        it("returns the original angle") {
            checkAll(Arb.numericDouble()) { magnitude ->
                val angle = magnitude.degrees
                angle.inDegrees().shouldBe(angle)
            }
        }
    }

    describe("converting to a string") {
        context("with an arbitrary finite magnitude") {
            it("has no whitespace and ends with the degree symbol") {
                checkAll(Arb.numericDouble()) { double ->
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
    }

    describe("adding two angles") {
        it("sums the magnitudes in the same unit") {
            checkAll(
                Arb.numericDouble(-1e15, 1e15),
                Arb.numericDouble(-1e15, 1e15)
            ) { magnitude1, magnitude2 ->
                val result = magnitude1.degrees + magnitude2.degrees
                result.shouldBe((magnitude1 + magnitude2).degrees)
            }
        }
    }
})
