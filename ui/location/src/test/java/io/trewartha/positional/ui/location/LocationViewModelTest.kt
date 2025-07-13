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
import io.trewartha.positional.ui.core.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock.System.now

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
    fun `Initial location state is loading`() {
        subject.location.value.shouldBe(State.Loading)
    }

    @Test
    fun `Location state is emitted once loaded`() = runTest {
        val expectedLocation = Location(now(), GeodeticCoordinates(1.degrees, 2.degrees))

        subject.location.test {
            awaitItem() // Loading state

            locator.setLocation(expectedLocation)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Location>>()
            state.data.shouldBe(expectedLocation)
        }
    }

    @Test
    fun `Location state is emitted when location changes`() = runTest {
        val expectedLocation = Location(now(), GeodeticCoordinates(1.degrees, 1.degrees))
        subject.location.test {
            awaitItem() // Loading state
            locator.setLocation(Location(now(), GeodeticCoordinates(0.degrees, 0.degrees)))
            awaitItem() // Initial data state

            locator.setLocation(expectedLocation)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Location>>()
            state.data.shouldBe(expectedLocation)
        }
    }

    @Test
    fun `Initial settings state is loading`() {
        subject.settings.value.shouldBe(State.Loading)
    }

    @Test
    fun `Settings state is emitted once loaded`() = runTest {
        val expectedUnits = Units.METRIC
        val expectedCoordinatesFormat = CoordinatesFormat.DD
        val expectedLocationAccuracyVisibility = LocationAccuracyVisibility.SHOW

        subject.settings.test {
            awaitItem() // Loading state

            settings.setCoordinatesFormat(expectedCoordinatesFormat)
            settings.setUnits(expectedUnits)
            settings.setLocationAccuracyVisibility(expectedLocationAccuracyVisibility)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Settings>>()
            with(state.data) {
                coordinatesFormat.shouldBe(expectedCoordinatesFormat)
                units.shouldBe(expectedUnits)
                accuracyVisibility.shouldBe(expectedLocationAccuracyVisibility)
            }
        }
    }

    @Test
    fun `Settings state is emitted when coordinates format changes`() = runTest {
        val expectedCoordinatesFormat = CoordinatesFormat.DDM
        subject.settings.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            awaitItem() // Initial data state

            settings.setCoordinatesFormat(expectedCoordinatesFormat)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Settings>>()
            state.data.coordinatesFormat.shouldBe(expectedCoordinatesFormat)
        }
    }

    @Test
    fun `Settings state is emitted when units change`() = runTest {
        val expectedUnits = Units.IMPERIAL
        subject.settings.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            awaitItem() // Initial data state

            settings.setUnits(expectedUnits)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Settings>>()
            state.data.units.shouldBe(expectedUnits)
        }
    }

    @Test
    fun `Settings state is emitted when location accuracy visibility changes`() = runTest {
        val expectedVisibility = LocationAccuracyVisibility.HIDE
        subject.settings.test {
            awaitItem() // Loading state
            settings.setCoordinatesFormat(CoordinatesFormat.DD)
            settings.setUnits(Units.METRIC)
            settings.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)
            awaitItem() // Initial data state

            settings.setLocationAccuracyVisibility(expectedVisibility)

            val state = awaitItem()
            state.shouldBeInstanceOf<State.Loaded<Settings>>()
            state.data.accuracyVisibility.shouldBe(expectedVisibility)
        }
    }
}
