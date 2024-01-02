package io.trewartha.positional.data.settings

import app.cash.turbine.test
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.ui.LocationAccuracyVisibility
import io.trewartha.positional.data.ui.Theme
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DataStoreSettingsRepositoryTest {

    private lateinit var subject: SettingsRepository

    @Before
    fun setUp() {
        subject = DataStoreSettingsRepository(RuntimeEnvironment.getApplication())
    }

    @Test
    fun testCompassModeDefaultAndSetAndGet() = runTest {
        subject.compassMode.test {
            withClue("Default compass mode should be true north") {
                awaitItem().shouldBe(CompassMode.TRUE_NORTH)
            }

            subject.setCompassMode(CompassMode.MAGNETIC_NORTH)

            awaitItem().shouldBe(CompassMode.MAGNETIC_NORTH)

            subject.setCompassMode(CompassMode.TRUE_NORTH)

            awaitItem().shouldBe(CompassMode.TRUE_NORTH)
        }
    }

    @Test
    fun testCoordinatesFormatDefaultAndSetAndGet() = runTest {
        subject.coordinatesFormat.test {
            withClue("Default coordinates format should be decimal degrees") {
                awaitItem().shouldBe(CoordinatesFormat.DD)
            }

            subject.setCoordinatesFormat(CoordinatesFormat.DDM)

            awaitItem().shouldBe(CoordinatesFormat.DDM)

            subject.setCoordinatesFormat(CoordinatesFormat.DMS)

            awaitItem().shouldBe(CoordinatesFormat.DMS)

            subject.setCoordinatesFormat(CoordinatesFormat.MGRS)

            awaitItem().shouldBe(CoordinatesFormat.MGRS)

            subject.setCoordinatesFormat(CoordinatesFormat.UTM)

            awaitItem().shouldBe(CoordinatesFormat.UTM)

            subject.setCoordinatesFormat(CoordinatesFormat.DD)

            awaitItem().shouldBe(CoordinatesFormat.DD)
        }
    }

    @Test
    fun testLocationAccuracyVisibilityDefaultAndSetAndGet() = runTest {
        subject.locationAccuracyVisibility.test {
            withClue("Default location accuracy visibility value should be 'show'") {
                awaitItem().shouldBe(LocationAccuracyVisibility.SHOW)
            }

            subject.setLocationAccuracyVisibility(LocationAccuracyVisibility.HIDE)

            awaitItem().shouldBe(LocationAccuracyVisibility.HIDE)

            subject.setLocationAccuracyVisibility(LocationAccuracyVisibility.SHOW)

            awaitItem().shouldBe(LocationAccuracyVisibility.SHOW)
        }
    }

    @Test
    fun testThemeDefaultAndSetAndGet() = runTest {
        subject.theme.test {
            withClue("Default theme should be 'device'") {
                awaitItem().shouldBe(Theme.DEVICE)
            }

            subject.setTheme(Theme.LIGHT)

            awaitItem().shouldBe(Theme.LIGHT)

            subject.setTheme(Theme.DARK)

            awaitItem().shouldBe(Theme.DARK)

            subject.setTheme(Theme.DEVICE)

            awaitItem().shouldBe(Theme.DEVICE)
        }
    }

    @Test
    fun testUnitsDefaultAndSetAndGet() = runTest {
        subject.units.test {
            withClue("Default units should be metric") {
                awaitItem().shouldBe(Units.IMPERIAL)
            }

            subject.setUnits(Units.METRIC)

            awaitItem().shouldBe(Units.METRIC)

            subject.setUnits(Units.IMPERIAL)

            awaitItem().shouldBe(Units.IMPERIAL)
        }
    }
}
