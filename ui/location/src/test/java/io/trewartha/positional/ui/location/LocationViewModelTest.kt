package io.trewartha.positional.ui.location

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.data.settings.TestSettingsRepository
import io.trewartha.positional.model.core.measurement.GeodeticCoordinates
import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.core.measurement.degrees
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock.System.now
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
    fun `Initial state is loading`() {
        subject.state.value.shouldBe(LocationState.Loading)
    }

    @Test
    fun `Data emitted once loaded`() = runTest {
        val expectedLocation = Location(now(), GeodeticCoordinates(1.degrees, 2.degrees))
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
    fun `Data emitted when location changes`() = runTest {
        val expectedLocation = Location(now(), GeodeticCoordinates(1.degrees, 1.degrees))
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(now(), GeodeticCoordinates(0.degrees, 0.degrees)))
            awaitItem() // Initial data state

            locator.setLocation(expectedLocation)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.location.shouldBe(expectedLocation)
        }
    }

    @Test
    fun `Data emitted when coordinates format changes`() = runTest {
        val expectedCoordinatesFormat = CoordinatesFormat.DDM
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(now(), GeodeticCoordinates(0.degrees, 0.degrees)))
            awaitItem() // Initial data state

            settings.setCoordinatesFormat(expectedCoordinatesFormat)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.coordinatesFormat.shouldBe(expectedCoordinatesFormat)
        }
    }

    @Test
    fun `Data emitted when units change`() = runTest {
        val expectedUnits = Units.IMPERIAL
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(now(), GeodeticCoordinates(0.degrees, 0.degrees)))
            awaitItem() // Initial data state

            settings.setUnits(expectedUnits)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.units.shouldBe(expectedUnits)
        }
    }

    @Test
    fun `Data emitted when location accuracy visibility changes`() = runTest {
        val expectedVisibility = LocationAccuracyVisibility.HIDE
        subject.state.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            locator.setLocation(Location(now(), GeodeticCoordinates(0.degrees, 0.degrees)))
            awaitItem() // Initial data state

            settings.setLocationAccuracyVisibility(expectedVisibility)

            val data = awaitItem()
            data.shouldBeInstanceOf<LocationState.Data>()
            data.accuracyVisibility.shouldBe(expectedVisibility)
        }
    }
}
