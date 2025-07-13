package io.trewartha.positional.data.location

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.GeodeticCoordinates
import io.trewartha.positional.model.core.measurement.degrees
import io.trewartha.positional.model.core.measurement.meters
import io.trewartha.positional.model.location.Location
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock

class TestLocatorTest {

    private lateinit var subject: TestLocator

    @BeforeTest
    fun setUp() {
        subject = TestLocator()
    }

    @Test
    fun `Location flow emits the last set location`() = runTest {
        val firstLocation = Location(
            timestamp = Clock.System.now(),
            coordinates = GeodeticCoordinates(latitude = 1.degrees, longitude = 2.degrees),
            altitude = 3.meters,
            magneticDeclination = null
        )
        val secondLocation = firstLocation.copy(altitude = 4.meters)
        subject.setLocation(firstLocation)
        subject.setLocation(secondLocation)

        subject.location.firstOrNull().shouldBe(secondLocation)
    }

    @Test
    fun `Setting the location triggers emission of set value`() = runTest {
        subject.location.test {
            val location = Location(
                timestamp = Clock.System.now(),
                coordinates = GeodeticCoordinates(latitude = 1.degrees, longitude = 2.degrees),
                altitude = 3.meters,
                magneticDeclination = null
            )

            subject.setLocation(location)

            awaitItem().shouldBe(location)
        }
    }
}
