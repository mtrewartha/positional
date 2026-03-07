package io.trewartha.positional.compass

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import io.trewartha.positional.core.measurement.degrees

class AzimuthTest : DescribeSpec({

    describe("creating an azimuth") {
        context("with an angle of exactly zero") {
            it("succeeds") {
                shouldNotThrow<IllegalArgumentException> { Azimuth(0.degrees) }
            }
        }
        context("with an angle just below 360") {
            it("succeeds") {
                shouldNotThrow<IllegalArgumentException> { Azimuth(359.9999.degrees) }
            }
        }
        context("with a negative angle") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { Azimuth((-0.0001).degrees) }
            }
        }
        context("with an angle of exactly 360") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { Azimuth(360.degrees) }
            }
        }
        context("with an angle greater than 360") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { Azimuth(361.degrees) }
            }
        }
    }

    describe("the accelerometer accuracy") {
        context("when specified") {
            it("is stored and returned") {
                checkAll(Arb.enum<Azimuth.Accuracy>()) { accuracy ->
                    val azimuth = Azimuth(angle = 0.degrees, accelerometerAccuracy = accuracy)
                    azimuth.accelerometerAccuracy.shouldBe(accuracy)
                }
            }
        }
        context("when not specified") {
            it("is null") {
                Azimuth(angle = 0.degrees).accelerometerAccuracy.shouldBe(null)
            }
        }
    }

    describe("the magnetometer accuracy") {
        context("when specified") {
            it("is stored and returned") {
                checkAll(Arb.enum<Azimuth.Accuracy>()) { accuracy ->
                    val azimuth = Azimuth(angle = 0.degrees, magnetometerAccuracy = accuracy)
                    azimuth.magnetometerAccuracy.shouldBe(accuracy)
                }
            }
        }
        context("when not specified") {
            it("is null") {
                Azimuth(angle = 0.degrees).magnetometerAccuracy.shouldBe(null)
            }
        }
    }

    describe("adding an angle") {
        context("when the sum stays within the valid range") {
            it("returns an azimuth with the sum of the angles") {
                val result = Azimuth(45.degrees) + (-5).degrees
                result.shouldBe(Azimuth(40.degrees))
            }
        }
        context("when the sum would be negative") {
            it("wraps around to a positive angle") {
                val result = Azimuth(1.degrees) + (-2).degrees
                result.shouldBe(Azimuth(359.degrees))
            }
        }
        context("when the sum would be 360 or greater") {
            it("wraps around to an angle below 360") {
                val result = Azimuth(359.degrees) + 2.degrees
                result.shouldBe(Azimuth(1.degrees))
            }
        }
    }
})