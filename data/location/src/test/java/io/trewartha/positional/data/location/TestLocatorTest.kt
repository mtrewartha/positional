package io.trewartha.positional.data.location

import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.core.measurement.Distance
import io.trewartha.positional.model.location.Location
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestLocatorTest {

    private lateinit var subject: TestLocator

    @BeforeTest
    fun setUp() {
        subject = TestLocator()
    }

    @Test
    fun `Getting the location returns the last set location`() = runTest {
        val firstLocation = Location(
            timestamp = Clock.System.now(),
            coordinates = Coordinates(latitude = 1.0, longitude = 2.0),
            altitude = Distance.Meters(3.0f),
            magneticDeclination = null
        )
        val secondLocation = firstLocation.copy(altitude = Distance.Meters(4.0f))
        subject.setLocation(firstLocation)
        subject.setLocation(secondLocation)

        subject.location.firstOrNull().shouldBe(secondLocation)
    }

    @Test
    fun `Setting the location updates the location`() = runTest {
        val location = Location(
            timestamp = Clock.System.now(),
            coordinates = Coordinates(latitude = 1.0, longitude = 2.0),
            altitude = Distance.Meters(3.0f),
            magneticDeclination = null
        )

        subject.setLocation(location)

        subject.location.firstOrNull().shouldBe(location)
    }
}
