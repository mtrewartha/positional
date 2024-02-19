package io.trewartha.positional.ui.location

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.data.settings.TestSettingsRepository
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocationViewModelTest {

    private lateinit var locator: TestLocator
    private lateinit var settings: SettingsRepository
    private lateinit var subject: LocationViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        locator = TestLocator()
        settings = TestSettingsRepository()

        subject = LocationViewModel(locator, settings)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadingState() {
        subject.state.value.shouldBe(LocationState.Loading)
    }

    @Test
    fun testDataEmittedOnceLoaded() = runTest {
        val expectedLocation = Location(Clock.System.now(), Coordinates(1.0, 2.0))
        val expectedUnits = Units.METRIC
        val expectedCoordinatesFormat = CoordinatesFormat.DD
        val expectedLocationAccuracyVisibility = LocationAccuracyVisibility.SHOW
        subject.state.test {
            awaitItem() // Loading state

            settings.setCoordinatesFormat(expectedCoordinatesFormat)
            settings.setUnits(expectedUnits)
            settings.setLocationAccuracyVisibility(expectedLocationAccuracyVisibility)
            locator.setLocation(expectedLocation)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.location.shouldBe(expectedLocation)
            data.coordinatesFormat.shouldBe(expectedCoordinatesFormat)
            data.units.shouldBe(expectedUnits)
            data.accuracyVisibility.shouldBe(expectedLocationAccuracyVisibility)
        }
    }

    @Test
    fun testDataEmittedWhenLocationChanges() = runTest {
        val expectedLocation = Location(Clock.System.now(), Coordinates(1.0, 1.0))
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(Clock.System.now(), Coordinates(0.0, 0.0)))
            awaitItem() // Initial data state

            locator.setLocation(expectedLocation)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.location.shouldBe(expectedLocation)
        }
    }

    @Test
    fun testDataEmittedWhenCoordinatesFormatChanges() = runTest {
        val expectedCoordinatesFormat = CoordinatesFormat.DDM
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(Clock.System.now(), Coordinates(0.0, 0.0)))
            awaitItem() // Initial data state

            settings.setCoordinatesFormat(expectedCoordinatesFormat)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.coordinatesFormat.shouldBe(expectedCoordinatesFormat)
        }
    }

    @Test
    fun testDataEmittedWhenUnitsChange() = runTest {
        val expectedUnits = Units.IMPERIAL
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(Clock.System.now(), Coordinates(0.0, 0.0)))
            awaitItem() // Initial data state

            settings.setUnits(expectedUnits)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.units.shouldBe(expectedUnits)
        }
    }

    @Test
    fun testDataEmittedWhenLocationAccuracyVisibilityChanges() = runTest {
        val expectedVisibility = LocationAccuracyVisibility.HIDE
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(Clock.System.now(), Coordinates(0.0, 0.0)))
            awaitItem() // Initial data state

            settings.setLocationAccuracyVisibility(expectedVisibility)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.accuracyVisibility.shouldBe(expectedVisibility)
        }
    }
}
