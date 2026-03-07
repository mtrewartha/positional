package io.trewartha.positional.compass

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.firstOrNull

class TestCompassTest : DescribeSpec({

    fun sut(): TestCompass = TestCompass()

    describe("the azimuth flow") {
        context("when setAzimuth is called") {
            it("emits the set azimuth") {
                val subject = sut()
                val azimuth = randomAzimuth()
                subject.azimuth.test {
                    subject.setAzimuth(azimuth)
                    awaitItem().shouldBe(azimuth)
                }
            }
        }

        context("when two different azimuths have been set in sequence") {
            it("emits only the latest azimuth") {
                val subject = sut()
                val firstAzimuth = randomAzimuth()
                val secondAzimuth = randomAzimuth()
                subject.setAzimuth(firstAzimuth)
                subject.setAzimuth(secondAzimuth)
                subject.azimuth.firstOrNull().shouldBe(secondAzimuth)
            }
        }

        context("when the same azimuth is set twice") {
            it("only emits once") {
                val subject = sut()
                val azimuth = randomAzimuth()
                subject.azimuth.test {
                    subject.setAzimuth(azimuth)
                    awaitItem() // consume first emission
                    subject.setAzimuth(azimuth)
                    expectNoEvents()
                }
            }
        }
    }
})

