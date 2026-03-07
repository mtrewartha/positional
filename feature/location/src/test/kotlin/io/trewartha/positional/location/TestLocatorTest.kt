package io.trewartha.positional.location

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.measurement.meters
import kotlinx.coroutines.flow.firstOrNull

class TestLocatorTest : DescribeSpec({

    fun sut(): TestLocator = TestLocator()

    describe("the location flow") {
        context("when a location is set") {
            it("emits the set location") {
                val subject = sut()
                val location = randomLocation()
                subject.location.test {
                    subject.setLocation(location)
                    awaitItem().shouldBe(location)
                }
            }
        }

        context("when two different locations are set in sequence") {
            it("emits only the latest location") {
                val subject = sut()
                val firstLocation = randomLocation()
                val secondLocation = randomLocation()
                subject.setLocation(firstLocation)
                subject.setLocation(secondLocation)
                subject.location.firstOrNull().shouldBe(secondLocation)
            }
        }

        context("when the same location is set twice") {
            it("only emits once") {
                val subject = sut()
                val location = randomLocation()
                subject.location.test {
                    subject.setLocation(location)
                    awaitItem() // consume first emission
                    subject.setLocation(location)
                    expectNoEvents()
                }
            }
        }

        context("when no specific location is provided") {
            it("emits a location at the origin with zero altitude") {
                val subject = sut()
                subject.location.test {
                    subject.setLocation()
                    val emitted = awaitItem()
                    emitted.coordinates.shouldBe(
                        GeodeticCoordinates(latitude = 0.degrees, longitude = 0.degrees)
                    )
                    emitted.altitude.shouldBe(0.meters)
                }
            }
        }
    }
})
