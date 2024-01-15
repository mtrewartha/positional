package io.trewartha.positional.ui.compass

import app.cash.turbine.test
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.data.compass.Azimuth
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.compass.TestCompass
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.settings.TestSettingsRepository
import io.trewartha.positional.data.ui.CompassNorthVibration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class CompassViewModelTest {

    private lateinit var subject: CompassViewModel

    private lateinit var compass: TestCompass
    private lateinit var locator: TestLocator
    private lateinit var settings: TestSettingsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        compass = TestCompass()
        locator = TestLocator()
        settings = TestSettingsRepository()

        subject = CompassViewModel(compass, locator, settings)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateIsSensorMissingStateWhenCompassMissing() {
        subject = CompassViewModel(null, locator, settings)

        subject.state.value.shouldBeInstanceOf<CompassState.SensorsMissing>()
    }

    @Test
    fun testInitialStateIsLoadingStateWhenCompassPresent() {
        subject.state.value.shouldBeInstanceOf<CompassState.Loading>()
    }

    @Test
    fun testDataEmittedOnceLoaded() = runTest {
        val expectedAzimuth = Azimuth(
            angle = Angle.Degrees(0f),
            accelerometerAccuracy = CompassAccuracy.HIGH,
            magnetometerAccuracy = CompassAccuracy.HIGH
        )
        val expectedCompassMode = CompassMode.MAGNETIC_NORTH
        val expectedCompassNorthVibration = CompassNorthVibration.NONE
        subject.state.test {
            awaitItem() // Loading state

            settings.setCompassMode(expectedCompassMode)
            settings.setCompassNorthVibration(expectedCompassNorthVibration)
            compass.setAzimuth(expectedAzimuth)

            val state = awaitItem()
            state.shouldBeInstanceOf<CompassState.Data>()
            state.azimuth.shouldBe(expectedAzimuth)
            state.declination.shouldBeNull()
            state.mode.shouldBe(expectedCompassMode)
            state.northVibration.shouldBe(expectedCompassNorthVibration)
        }
    }

    @Test
    fun testDataEmittedWhenAzimuthChanges() = runTest {
        val initialAzimuth = Azimuth(
            angle = Angle.Degrees(1f),
            accelerometerAccuracy = CompassAccuracy.HIGH,
            magnetometerAccuracy = CompassAccuracy.HIGH
        )
        val expectedAzimuth = initialAzimuth.copy(angle = Angle.Degrees(2f))
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(initialAzimuth)
            awaitItem() // Initial data state

            compass.setAzimuth(expectedAzimuth)

            val result = awaitItem()
            result.shouldBeInstanceOf<CompassState.Data>()
            result.azimuth.shouldBe(expectedAzimuth)
        }
    }

    @Test
    fun testDataEmittedWhenDeclinationChanges() = runTest {
        val expectedDeclination = Angle.Degrees(1f)
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(Angle.Degrees(0f)))
            awaitItem() // Initial data state

            locator.setLocation(
                Location(
                    timestamp = Clock.System.now(),
                    coordinates = Coordinates(0.0, 0.0),
                    magneticDeclination = expectedDeclination
                )
            )

            val result = awaitItem()
            result.shouldBeInstanceOf<CompassState.Data>()
            result.declination.shouldBe(expectedDeclination)
        }
    }

    @Test
    fun testDataEmittedWhenCompassModeChanges() = runTest {
        val expectedMode = CompassMode.TRUE_NORTH
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(Angle.Degrees(0f)))
            awaitItem() // Initial data state

            settings.setCompassMode(expectedMode)

            val result = awaitItem()
            result.shouldBeInstanceOf<CompassState.Data>()
            result.mode.shouldBe(expectedMode)
        }
    }

    @Test
    fun testDataEmittedWhenCompassNorthVibrationChanges() = runTest {
        val expectedVibration = CompassNorthVibration.SHORT
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(Angle.Degrees(0f)))
            awaitItem() // Initial data state

            settings.setCompassNorthVibration(expectedVibration)

            val result = awaitItem()
            result.shouldBeInstanceOf<CompassState.Data>()
            result.northVibration.shouldBe(expectedVibration)
        }
    }
}
