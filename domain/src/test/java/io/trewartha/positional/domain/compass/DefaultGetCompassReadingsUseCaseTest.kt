package io.trewartha.positional.domain.compass

import app.cash.turbine.test
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.compass.CompassAzimuth
import io.trewartha.positional.data.compass.TestCompass
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.measurement.Angle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class DefaultGetCompassReadingsUseCaseTest {

    private lateinit var compass: TestCompass
    private lateinit var locator: TestLocator
    private lateinit var subject: GetCompassReadingsUseCase

    @BeforeTest
    fun setUp() {
        compass = TestCompass()
        locator = TestLocator()
        subject = DefaultGetCompassReadingsUseCase(compass, locator)
    }

    @Test
    fun testEmissionWithAzimuthEvenIfLocationHasNotBeenDeterminedYet() = runTest {
        subject().test {
            expectNoEvents()

            val azimuth = CompassAzimuth(Angle.Degrees(0f))
            compass.setAzimuth(azimuth)

            with(awaitItem()) {
                magneticAzimuth.shouldBe(azimuth)
                magneticDeclination.shouldBeNull()
            }
        }
    }

    @Test
    fun testLatestAzimuthIsCombinedWithLatestMagneticDeclination() = runTest {
        val firstAzimuth = CompassAzimuth(Angle.Degrees(0f))
        compass.setAzimuth(firstAzimuth)

        val firstLocation = Location(
            Clock.System.now() - 10.minutes,
            Coordinates(1.0, 2.0),
            magneticDeclination = Angle.Degrees(3f)
        )
        locator.setLocation(firstLocation)

        subject().test {
            with(awaitItem()) {
                magneticAzimuth.shouldBe(firstAzimuth)
                magneticDeclination.shouldBe(firstLocation.magneticDeclination)
            }

            val secondAzimuth = CompassAzimuth(Angle.Degrees(90f))
            compass.setAzimuth(secondAzimuth)

            with(awaitItem()) {
                magneticAzimuth.shouldBe(secondAzimuth)
                magneticDeclination.shouldBe(firstLocation.magneticDeclination)
            }

            val secondLocation = Location(
                Clock.System.now(),
                Coordinates(10.0, 20.0),
                magneticDeclination = Angle.Degrees(30f)
            )
            locator.setLocation(secondLocation)

            with(awaitItem()) {
                magneticAzimuth.shouldBe(secondAzimuth)
                magneticDeclination.shouldBe(secondLocation.magneticDeclination)
            }
        }
    }
}
