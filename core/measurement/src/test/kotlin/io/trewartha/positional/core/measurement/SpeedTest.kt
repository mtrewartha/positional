package io.trewartha.positional.core.measurement

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.checkAll
import kotlin.math.abs

class SpeedTest : DescribeSpec({

    describe("creating a speed in kilometers per hour") {
        it("creates a speed with the correct magnitude and unit") {
            val numbers = setOf<Number>(1, 1.23f, 1.23)
            for (number in numbers) {
                number.kph.shouldBe(Speed(number.toDouble(), Speed.Unit.KILOMETERS_PER_HOUR))
            }
        }
    }

    describe("creating a speed in meters per second") {
        it("creates a speed with the correct magnitude and unit") {
            val numbers = setOf<Number>(1, 1.23f, 1.23)
            for (number in numbers) {
                number.mps.shouldBe(Speed(number.toDouble(), Speed.Unit.METERS_PER_SECOND))
            }
        }
    }

    describe("creating a speed in miles per hour") {
        it("creates a speed with the correct magnitude and unit") {
            val numbers = setOf<Number>(1, 1.23f, 1.23)
            for (number in numbers) {
                number.mph.shouldBe(Speed(number.toDouble(), Speed.Unit.MILES_PER_HOUR))
            }
        }
    }

    describe("converting to kilometers per hour") {
        context("when the speed is already in kilometers per hour") {
            it("returns the original speed") {
                checkAll(Arb.double()) { magnitude ->
                    magnitude.kph.inKilometersPerHour().shouldBe(magnitude.kph)
                }
            }
        }

        context("when the speed is in meters per second") {
            it("returns the correct converted speed") {
                1.mps.inKilometersPerHour().shouldBe(Speed(3.6, Speed.Unit.KILOMETERS_PER_HOUR))
            }

            it("is reversible for any finite magnitude") {
                // Bounded: full-range inputs overflow to Infinity when multiplied by the
                // conversion factor (3.6). Tolerance floor 1e-290 prevents the relative
                // tolerance from rounding down to 0.0 for subnormal magnitudes.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.mps.inKilometersPerHour().inMetersPerSecond().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }

        context("when the speed is in miles per hour") {
            it("returns the correct converted speed") {
                1.mph.inKilometersPerHour().shouldBe(Speed(1.60934, Speed.Unit.KILOMETERS_PER_HOUR))
            }

            it("is reversible for any finite magnitude") {
                // Bounded: full-range inputs overflow to Infinity when multiplied by the
                // conversion factor (1.60934). Tolerance floor 1e-290 prevents the relative
                // tolerance from rounding down to 0.0 for subnormal magnitudes.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.mph.inKilometersPerHour().inMilesPerHour().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }
    }

    describe("converting to meters per second") {
        context("when the speed is in kilometers per hour") {
            it("returns the correct converted speed") {
                1.kph.inMetersPerSecond().shouldBe(Speed(0.277778, Speed.Unit.METERS_PER_SECOND))
            }

            it("is reversible for any finite magnitude") {
                // Bounded for consistency with other round-trip tests. Tolerance floor 1e-290
                // prevents the relative tolerance from rounding down to 0.0 for subnormals.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.kph.inMetersPerSecond().inKilometersPerHour().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }

        context("when the speed is already in meters per second") {
            it("returns the original speed") {
                checkAll(Arb.double()) { magnitude ->
                    magnitude.mps.inMetersPerSecond().shouldBe(magnitude.mps)
                }
            }
        }

        context("when the speed is in miles per hour") {
            it("returns the correct converted speed") {
                1.mph.inMetersPerSecond().shouldBe(Speed(0.44704, Speed.Unit.METERS_PER_SECOND))
            }

            it("is reversible for any finite magnitude") {
                // Bounded for consistency with other round-trip tests. Tolerance floor 1e-290
                // prevents the relative tolerance from rounding down to 0.0 for subnormals.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.mph.inMetersPerSecond().inMilesPerHour().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }
    }

    describe("converting to miles per hour") {
        context("when the speed is in kilometers per hour") {
            it("returns the correct converted speed") {
                1.kph.inMilesPerHour().shouldBe(Speed(0.621371, Speed.Unit.MILES_PER_HOUR))
            }

            it("is reversible for any finite magnitude") {
                // Bounded for consistency with other round-trip tests. Tolerance floor 1e-290
                // prevents the relative tolerance from rounding down to 0.0 for subnormals.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.kph.inMilesPerHour().inKilometersPerHour().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }

        context("when the speed is in meters per second") {
            it("returns the correct converted speed") {
                1.mps.inMilesPerHour().shouldBe(Speed(2.236936, Speed.Unit.MILES_PER_HOUR))
            }

            it("is reversible for any finite magnitude") {
                // Bounded: full-range inputs overflow to Infinity when multiplied by the
                // conversion factor (2.236936). Tolerance floor 1e-290 prevents the relative
                // tolerance from rounding down to 0.0 for subnormal magnitudes.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.mps.inMilesPerHour().inMetersPerSecond().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }

        context("when the speed is already in miles per hour") {
            it("returns the original speed") {
                checkAll(Arb.double()) { magnitude ->
                    magnitude.mph.inMilesPerHour().shouldBe(magnitude.mph)
                }
            }
        }
    }

    describe("converting to a string") {
        context("when the speed is in kilometers per hour") {
            it("ends with the unit abbreviation and has no whitespace in the numeric portion") {
                checkAll(Arb.double()) { magnitude ->
                    val result = magnitude.kph.toString()
                    result.shouldEndWith(" km/h")
                    result.substringBeforeLast(" ").shouldNotContain(Regex("\\s"))
                }
            }
        }

        context("when the speed is in meters per second") {
            it("ends with the unit abbreviation and has no whitespace in the numeric portion") {
                checkAll(Arb.double()) { magnitude ->
                    val result = magnitude.mps.toString()
                    result.shouldEndWith(" m/s")
                    result.substringBeforeLast(" ").shouldNotContain(Regex("\\s"))
                }
            }
        }

        context("when the speed is in miles per hour") {
            it("ends with the unit abbreviation and has no whitespace in the numeric portion") {
                checkAll(Arb.double()) { magnitude ->
                    val result = magnitude.mph.toString()
                    result.shouldEndWith(" mi/h")
                    result.substringBeforeLast(" ").shouldNotContain(Regex("\\s"))
                }
            }
        }
    }
})
