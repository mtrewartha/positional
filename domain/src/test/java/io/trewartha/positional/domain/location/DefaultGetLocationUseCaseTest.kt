package io.trewartha.positional.domain.location

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.TestLocator
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultGetLocationUseCaseTest {

    private lateinit var locator: TestLocator
    private lateinit var subject: GetLocationUseCase

    @BeforeTest
    fun setUp() {
        locator = TestLocator()
        subject = DefaultGetLocationUseCase(locator)
    }

    @Test
    fun testEmissionWheneverLocationChanges() = runTest {
        val location1 = Location(Clock.System.now(), Coordinates(1.0, 1.0))
        val location2 = Location(Clock.System.now(), Coordinates(2.0, 2.0))

        subject().test {
            expectNoEvents()
            locator.setLocation(location1)
            awaitItem().shouldBe(location1)
            locator.setLocation(location2)
            awaitItem().shouldBe(location2)
        }
    }
}
