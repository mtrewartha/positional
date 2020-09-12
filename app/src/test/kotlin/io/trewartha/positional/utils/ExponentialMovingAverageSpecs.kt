package io.trewartha.positional.utils

import com.google.common.collect.EvictingQueue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.arbitrary.numericFloats
import io.kotest.property.checkAll
import io.kotest.property.forAll

@Suppress("UnstableApiUsage")
class ExponentialMovingAverageSpecs : DescribeSpec({
    describe("exponentialMovingAverage") {
        describe("of an evicting queue of floats") {
            lateinit var evictingQueue: EvictingQueue<Float>

            describe("with an alpha <= 0 or >= 1") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(1)
                    evictingQueue.add(1f)
                }

                it("throws an IllegalArgumentException") {
                    checkAll(Arb.float()) { alpha ->
                        when {
                            alpha.isNaN() ||
                                    alpha == Float.NEGATIVE_INFINITY ||
                                    alpha == Float.POSITIVE_INFINITY ||
                                    alpha == Float.MIN_VALUE ||
                                    alpha == Float.MIN_VALUE ||
                                    alpha == 0.0f ||
                                    alpha == 1.0f ||
                                    alpha <= 0f ||
                                    alpha >= 1f -> {
                                shouldThrow<IllegalArgumentException> {
                                    evictingQueue.ema(alpha)
                                }
                            }
                            else -> {
                                evictingQueue.ema(alpha)!!.isFinite()
                            }
                        }
                    }
                }
            }
            describe("that has a length of zero") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(0)
                }

                it("returns null") {
                    evictingQueue.ema(0f).shouldBeNull()
                }
            }
            describe("that has a length greater than zero") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(5)
                }
                describe("and is empty") {
                    it("returns null") {
                        forAll(Arb.numericFloats(0f, 1f)) {
                            evictingQueue.ema(it) == null
                        }
                    }
                }
                describe("and is not empty") {
                    beforeEach {
                        evictingQueue.addAll(sequenceOf(1f, 2f, 3f, 4f, 5f))
                    }
                    it("returns the correct average of the floats") {
                        evictingQueue.ema(.5f).shouldBe(4.0625f)
                    }
                }
            }
        }

        describe("of an evicting queue of doubles") {
            lateinit var evictingQueue: EvictingQueue<Double>

            describe("with an alpha <= 0 or >= 1") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(1)
                    evictingQueue.add(1.0)
                }

                it("throws an IllegalArgumentException") {
                    checkAll(Arb.double()) { alpha ->
                        when {
                            alpha.isNaN() ||
                                    alpha == Double.NEGATIVE_INFINITY ||
                                    alpha == Double.POSITIVE_INFINITY ||
                                    alpha == Double.MIN_VALUE ||
                                    alpha == Double.MIN_VALUE ||
                                    alpha == 0.0 ||
                                    alpha == 1.0 ||
                                    alpha <= 0.0 ||
                                    alpha >= 1.0 -> {
                                shouldThrow<IllegalArgumentException> {
                                    evictingQueue.ema(alpha)
                                }
                            }
                            else -> {
                                evictingQueue.ema(alpha)!!.isFinite()
                            }
                        }
                    }
                }
            }
            describe("that has a length of zero") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(0)
                }

                it("returns null") {
                    evictingQueue.ema(0.0).shouldBeNull()
                }
            }
            describe("that has a length greater than zero") {
                beforeEach {
                    evictingQueue = EvictingQueue.create(5)
                }
                describe("and is empty") {
                    it("returns null") {
                        forAll(Arb.numericDoubles(0.0, 1.0)) {
                            evictingQueue.ema(it) == null
                        }
                    }
                }
                describe("and is not empty") {
                    beforeEach {
                        evictingQueue.addAll(sequenceOf(1.0, 2.0, 3.0, 4.0, 5.0))
                    }
                    it("returns the correct average of the doubles") {
                        evictingQueue.ema(.5).shouldBe(4.0625)
                    }
                }
            }
        }
    }
})