package io.trewartha.positional.core.measurement

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.negativeDouble
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.positiveDouble
import io.kotest.property.checkAll
import kotlin.math.abs

class DistanceTest : DescribeSpec({

    describe("creating a distance in feet") {
        it("creates a distance with the correct magnitude and unit") {
            checkAll(Arb.double()) { number ->
                number.feet.shouldBe(Distance(number, Distance.Unit.FEET))
            }
        }
    }

    describe("creating a distance in meters") {
        it("creates a distance with the correct magnitude and unit") {
            checkAll(Arb.double()) { number ->
                number.meters.shouldBe(Distance(number, Distance.Unit.METERS))
            }
        }
    }

    describe("checking whether the distance is negative") {
        context("when the magnitude is negative") {
            it("returns true") {
                checkAll(
                    Arb.negativeDouble().filter { !it.isNaN() },
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isNegative.shouldBeTrue()
                }
            }
        }

        context("when the magnitude is zero") {
            it("returns false") {
                checkAll(Arb.enum<Distance.Unit>()) { unit ->
                    Distance(0.0, unit).isNegative.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is positive") {
            it("returns false") {
                checkAll(
                    Arb.positiveDouble().filter { !it.isNaN() },
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isNegative.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is NaN") {
            it("returns false") {
                checkAll(Arb.enum<Distance.Unit>()) { unit ->
                    Distance(Double.NaN, unit).isNegative.shouldBeFalse()
                }
            }
        }
    }

    describe("checking whether the distance is positive") {
        context("when the magnitude is negative") {
            it("returns false") {
                checkAll(
                    Arb.negativeDouble().filter { !it.isNaN() },
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isPositive.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is zero") {
            it("returns false") {
                checkAll(Arb.enum<Distance.Unit>()) { unit ->
                    Distance(0.0, unit).isPositive.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is positive") {
            it("returns true") {
                checkAll(
                    Arb.positiveDouble().filter { !it.isNaN() },
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isPositive.shouldBeTrue()
                }
            }
        }

        context("when the magnitude is NaN") {
            it("returns false") {
                checkAll(Arb.enum<Distance.Unit>()) { unit ->
                    Distance(Double.NaN, unit).isPositive.shouldBeFalse()
                }
            }
        }
    }

    describe("checking whether the distance is finite") {
        context("when the magnitude is NaN") {
            it("returns false") {
                checkAll(Arb.enum<Distance.Unit>()) { unit ->
                    Distance(Double.NaN, unit).isFinite.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is infinite") {
            it("returns false") {
                checkAll(
                    Arb.of(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isFinite.shouldBeFalse()
                }
            }
        }

        context("when the magnitude is finite") {
            it("returns true") {
                checkAll(
                    Arb.numericDouble(),
                    Arb.enum<Distance.Unit>()
                ) { magnitude, unit ->
                    Distance(magnitude, unit).isFinite.shouldBeTrue()
                }
            }
        }
    }

    describe("converting to feet") {
        context("when the distance is already in feet") {
            it("returns the original distance") {
                checkAll(Arb.double()) { magnitude ->
                    magnitude.feet.inFeet().shouldBe(magnitude.feet)
                }
            }
        }

        context("when the distance is in meters") {
            it("returns the correct distance in feet") {
                1.meters.inFeet().shouldBe(Distance(3.28084, Distance.Unit.FEET))
            }

            it("is reversible for any finite magnitude") {
                // Bounded: full-range inputs overflow to Infinity when multiplied by the
                // conversion factor (3.28084). Tolerance floor 1e-290 prevents the relative
                // tolerance from rounding down to 0.0 for subnormal magnitudes.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.meters.inFeet().inMeters().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }
    }

    describe("converting to meters") {
        context("when the distance is in feet") {
            it("returns the correct distance in meters") {
                1.feet.inMeters().shouldBe(Distance(0.3048, Distance.Unit.METERS))
            }

            it("is reversible for any finite magnitude") {
                // Bounded for consistency with the meters→feet round-trip test. Tolerance
                // floor 1e-290 prevents the relative tolerance from rounding down to 0.0
                // for subnormal magnitudes.
                checkAll(Arb.numericDouble(min = -1e100, max = 1e100)) { magnitude ->
                    val roundTripped = magnitude.feet.inMeters().inFeet().magnitude
                    roundTripped.shouldBe(magnitude plusOrMinus maxOf(abs(magnitude) * 1e-4, 1e-290))
                }
            }
        }

        context("when the distance is already in meters") {
            it("returns the original distance") {
                checkAll(Arb.double()) { magnitude ->
                    magnitude.meters.inMeters().shouldBe(magnitude.meters)
                }
            }
        }
    }
})
