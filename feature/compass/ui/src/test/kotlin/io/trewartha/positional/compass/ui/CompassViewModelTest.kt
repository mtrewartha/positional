package io.trewartha.positional.compass.ui

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.compass.Azimuth
import io.trewartha.positional.compass.TestCompass
import io.trewartha.positional.compass.randomAzimuth
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.location.TestLocator
import io.trewartha.positional.location.randomLocation
import io.trewartha.positional.settings.CompassMode
import io.trewartha.positional.settings.CompassNorthVibration
import io.trewartha.positional.settings.TestSettingsRepository

class CompassViewModelTest : DescribeSpec({

    fun sut(
        compass: TestCompass? = TestCompass(),
        locator: TestLocator = TestLocator(),
        settings: TestSettingsRepository = TestSettingsRepository(),
    ): CompassViewModel = CompassViewModel(compass, locator, settings)

    describe("the state") {
        context("when compass hardware is missing") {
            it("reflects sensors missing") {
                sut(compass = null).state.value
                    .shouldBeInstanceOf<State.Failure<CompassError.SensorsMissing>>()
            }
        }

        context("just after initialization with compass hardware present") {
            it("is loading") {
                sut().state.value.shouldBeInstanceOf<State.Loading>()
            }
        }

        context("when all dependencies emit values") {
            it("emits loaded data with the correct values") {
                val compass = TestCompass()
                val locator = TestLocator()
                val settings = TestSettingsRepository()
                val subject = sut(compass, locator, settings)
                val expectedAzimuth = randomAzimuth()
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
        }

        context("when the azimuth changes") {
            it("emits updated data with the new azimuth") {
                val compass = TestCompass()
                val settings = TestSettingsRepository()
                val subject = sut(compass = compass, settings = settings)
                val initialAzimuth = Azimuth(angle = 1.degrees)
                val expectedAzimuth = Azimuth(angle = 2.degrees)
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
        }

        context("when the declination changes") {
            it("emits updated data with the new declination") {
                val compass = TestCompass()
                val locator = TestLocator()
                val settings = TestSettingsRepository()
                val subject = sut(compass = compass, locator = locator, settings = settings)
                val expectedDeclination = 1.degrees
                subject.state.test {
                    awaitItem() // Loading state
                    settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
                    settings.setCompassNorthVibration(CompassNorthVibration.NONE)
                    compass.setAzimuth(randomAzimuth())
                    awaitItem() // Initial data state

                    locator.setLocation(randomLocation().copy(magneticDeclination = expectedDeclination))

                    val result = awaitItem()
                    result.shouldBeInstanceOf<State.Loaded<CompassData>>()
                    result.data.declination.shouldBe(expectedDeclination)
                }
            }
        }

        context("when the compass mode changes") {
            it("emits updated data with the new mode") {
                val compass = TestCompass()
                val settings = TestSettingsRepository()
                val subject = sut(compass = compass, settings = settings)
                val expectedMode = CompassMode.TRUE_NORTH
                subject.state.test {
                    awaitItem() // Loading state
                    settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
                    settings.setCompassNorthVibration(CompassNorthVibration.NONE)
                    compass.setAzimuth(randomAzimuth())
                    awaitItem() // Initial data state

                    settings.setCompassMode(expectedMode)

                    val result = awaitItem()
                    result.shouldBeInstanceOf<State.Loaded<CompassData>>()
                    result.data.mode.shouldBe(expectedMode)
                }
            }
        }

        context("when the compass north vibration changes") {
            it("emits updated data with the new vibration setting") {
                val compass = TestCompass()
                val settings = TestSettingsRepository()
                val subject = sut(compass = compass, settings = settings)
                val expectedVibration = CompassNorthVibration.SHORT
                subject.state.test {
                    awaitItem() // Loading state
                    settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
                    settings.setCompassNorthVibration(CompassNorthVibration.NONE)
                    compass.setAzimuth(randomAzimuth())
                    awaitItem() // Initial data state

                    settings.setCompassNorthVibration(expectedVibration)

                    val result = awaitItem()
                    result.shouldBeInstanceOf<State.Loaded<CompassData>>()
                    result.data.northVibration.shouldBe(expectedVibration)
                }
            }
        }

        context("when a location without declination is emitted after one with declination") {
            it("emits updated data with null declination") {
                val compass = TestCompass()
                val locator = TestLocator()
                val settings = TestSettingsRepository()
                val subject = sut(compass = compass, locator = locator, settings = settings)
                subject.state.test {
                    awaitItem() // Loading state
                    settings.setCompassMode(CompassMode.MAGNETIC_NORTH)
                    settings.setCompassNorthVibration(CompassNorthVibration.NONE)
                    compass.setAzimuth(randomAzimuth())
                    awaitItem() // Initial data state (no location yet, declination null)

                    locator.setLocation(randomLocation().copy(magneticDeclination = 5.degrees))
                    awaitItem() // Data state with non-null declination

                    locator.setLocation(randomLocation().copy(magneticDeclination = null))

                    val result = awaitItem()
                    result.shouldBeInstanceOf<State.Loaded<CompassData>>()
                    result.data.declination.shouldBeNull()
                }
            }
        }
    }
})
