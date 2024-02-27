package io.trewartha.positional.ui.compass

import app.cash.turbine.test
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.data.compass.TestCompass
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.settings.TestSettingsRepository
import io.trewartha.positional.model.compass.Azimuth
import io.trewartha.positional.model.core.measurement.GeodeticCoordinates
import io.trewartha.positional.model.core.measurement.degrees
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.model.settings.CompassNorthVibration
import io.trewartha.positional.ui.core.State
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
    fun `Initial state reflects sensors missing when compass missing`() {
        subject = CompassViewModel(null, locator, settings)

        subject.state.value.shouldBeInstanceOf<State.Error<CompassError.SensorsMissing>>()
    }

    @Test
    fun `Initial state is loading when compass present`() {
        subject.state.value.shouldBeInstanceOf<State.Loading>()
    }

    @Test
    fun `Data emitted once loaded`() = runTest {
        val expectedAzimuth = Azimuth(
            angle = 0.degrees,
            accelerometerAccuracy = Azimuth.Accuracy.HIGH,
            magnetometerAccuracy = Azimuth.Accuracy.HIGH
        )
        val expectedCompassMode = CompassMode.MAGNETIC_NORTH
        val expectedCompassNorthVibration = CompassNorthVibration.NONE
        subject.state.test {
            awaitItem() // Loading state

            settings.setCompassMode(expectedCompassMode)
            settings.setCompassNorthVibration(expectedCompassNorthVibration)
            compass.setAzimuth(expectedAzimuth)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<CompassData>>()
            with(state.data) {
                azimuth.shouldBe(expectedAzimuth)
                declination.shouldBeNull()
                mode.shouldBe(expectedCompassMode)
                northVibration.shouldBe(expectedCompassNorthVibration)
            }
        }
    }

    @Test
    fun `Data emitted when azimuth changes`() = runTest {
        val initialAzimuth = Azimuth(
            angle = 1.degrees,
            accelerometerAccuracy = Azimuth.Accuracy.HIGH,
            magnetometerAccuracy = Azimuth.Accuracy.HIGH
        )
        val expectedAzimuth = initialAzimuth.copy(angle = 2.degrees)
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(initialAzimuth)
            awaitItem() // Initial data state

            compass.setAzimuth(expectedAzimuth)

            val result = awaitItem()
            result.shouldBeInstanceOf<State.Loaded<CompassData>>()
            result.data.azimuth.shouldBe(expectedAzimuth)
        }
    }

    @Test
    fun `Data emitted when declination changes`() = runTest {
        val expectedDeclination = 1.degrees
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(0.degrees))
            awaitItem() // Initial data state

            locator.setLocation(
                Location(
                    timestamp = Clock.System.now(),
                    coordinates = GeodeticCoordinates(0.degrees, 0.degrees),
                    magneticDeclination = expectedDeclination
                )
            )

            val result = awaitItem()
            result.shouldBeInstanceOf<State.Loaded<CompassData>>()
            result.data.declination.shouldBe(expectedDeclination)
        }
    }

    @Test
    fun `Data emitted when compass mode changes`() = runTest {
        val expectedMode = CompassMode.TRUE_NORTH
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(0.degrees))
            awaitItem() // Initial data state

            settings.setCompassMode(expectedMode)

            val result = awaitItem()
            result.shouldBeInstanceOf<State.Loaded<CompassData>>()
            result.data.mode.shouldBe(expectedMode)
        }
    }

    @Test
    fun `Data emitted when compass north vibration changes`() = runTest {
        val expectedVibration = CompassNorthVibration.SHORT
        subject.state.test {
            awaitItem() // Loading state
            settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
            settings.setCompassNorthVibration(CompassNorthVibration.NONE)
            compass.setAzimuth(Azimuth(0.degrees))
            awaitItem() // Initial data state

            settings.setCompassNorthVibration(expectedVibration)

            val result = awaitItem()
            result.shouldBeInstanceOf<State.Loaded<CompassData>>()
            result.data.northVibration.shouldBe(expectedVibration)
        }
    }
}
