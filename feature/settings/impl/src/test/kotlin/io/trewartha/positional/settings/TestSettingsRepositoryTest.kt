package io.trewartha.positional.settings

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.Units
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestSettingsRepositoryTest {

    private lateinit var subject: TestSettingsRepository

    @BeforeTest
    fun setUp() {
        subject = TestSettingsRepository()
    }

    @Test
    fun `Compass mode flow emits the last set compass mode`() = runTest {
        val firstCompassMode = CompassMode.MAGNETIC_NORTH
        val secondCompassMode = CompassMode.TRUE_NORTH
        subject.setCompassMode(firstCompassMode)
        subject.setCompassMode(secondCompassMode)

        subject.compassMode.firstOrNull().shouldBe(secondCompassMode)
    }

    @Test
    fun `Setting the compass mode triggers emission of set value`() = runTest {
        subject.compassMode.test {
            val compassMode = CompassMode.MAGNETIC_NORTH

            subject.setCompassMode(compassMode)

            awaitItem().shouldBe(compassMode)
        }
    }

    @Test
    fun `Compass north vibration flow emits the last set compass north vibration`() = runTest {
        val firstCompassNorthVibration = CompassNorthVibration.LONG
        val secondCompassNorthVibration = CompassNorthVibration.SHORT
        subject.setCompassNorthVibration(firstCompassNorthVibration)
        subject.setCompassNorthVibration(secondCompassNorthVibration)

        subject.compassNorthVibration.firstOrNull().shouldBe(secondCompassNorthVibration)
    }

    @Test
    fun `Setting the compass north vibration triggers emission of set value`() = runTest {
        subject.compassNorthVibration.test {
            val compassNorthVibration = CompassNorthVibration.SHORT

            subject.setCompassNorthVibration(compassNorthVibration)

            awaitItem().shouldBe(compassNorthVibration)
        }
    }

    @Test
    fun `Coordinates format flow emits the last set coordinates format`() = runTest {
        val firstCoordinatesFormat = CoordinatesFormat.DD
        val secondCoordinatesFormat = CoordinatesFormat.DDM
        subject.setCoordinatesFormat(firstCoordinatesFormat)
        subject.setCoordinatesFormat(secondCoordinatesFormat)

        subject.coordinatesFormat.firstOrNull().shouldBe(secondCoordinatesFormat)
    }

    @Test
    fun `Setting the coordinates format triggers emission of set value`() = runTest {
        subject.coordinatesFormat.test {
            val coordinatesFormat = CoordinatesFormat.DMS

            subject.setCoordinatesFormat(coordinatesFormat)

            awaitItem().shouldBe(coordinatesFormat)
        }
    }

    @Test
    fun `Location accuracy visibility flow emits the last set visibility`() = runTest {
        val firstVisibility = LocationAccuracyVisibility.SHOW
        val secondVisibility = LocationAccuracyVisibility.HIDE
        subject.setLocationAccuracyVisibility(firstVisibility)
        subject.setLocationAccuracyVisibility(secondVisibility)

        subject.locationAccuracyVisibility.firstOrNull().shouldBe(secondVisibility)
    }

    @Test
    fun `Setting the location accuracy visibility triggers emission of set value`() = runTest {
        subject.locationAccuracyVisibility.test {
            val visibility = LocationAccuracyVisibility.HIDE

            subject.setLocationAccuracyVisibility(visibility)

            awaitItem().shouldBe(visibility)
        }
    }

    @Test
    fun `Theme flow emits the last set theme`() = runTest {
        val firstTheme = Theme.DARK
        val secondTheme = Theme.LIGHT
        subject.setTheme(firstTheme)
        subject.setTheme(secondTheme)

        subject.theme.firstOrNull().shouldBe(secondTheme)
    }

    @Test
    fun `Setting the theme triggers emission of set value`() = runTest {
        subject.theme.test {
            val theme = Theme.LIGHT

            subject.setTheme(theme)

            awaitItem().shouldBe(theme)
        }
    }

    @Test
    fun `Units flow emits the last set units`() = runTest {
        val firstUnits = Units.METRIC
        val secondUnits = Units.IMPERIAL
        subject.setUnits(firstUnits)
        subject.setUnits(secondUnits)

        subject.units.firstOrNull().shouldBe(secondUnits)
    }

    @Test
    fun `Setting the units triggers emission of set value`() = runTest {
        subject.units.test {
            val units = Units.METRIC

            subject.setUnits(units)

            awaitItem().shouldBe(units)
        }
    }
}
