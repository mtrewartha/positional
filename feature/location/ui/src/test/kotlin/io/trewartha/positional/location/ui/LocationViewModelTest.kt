package io.trewartha.positional.location.ui

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.location.Location
import io.trewartha.positional.location.TestLocator
import io.trewartha.positional.location.randomLocation
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility
import io.trewartha.positional.settings.TestSettingsRepository
import io.trewartha.positional.settings.randomCoordinatesFormat
import io.trewartha.positional.settings.randomLocationAccuracyVisibility
import io.trewartha.positional.settings.randomUnits

class LocationViewModelTest : DescribeSpec({

    fun sut(
        locator: TestLocator = TestLocator(),
        settings: TestSettingsRepository = TestSettingsRepository(),
    ): LocationViewModel = LocationViewModel(locator, settings)

    describe("the location state") {
        context("just after initialization") {
            it("is loading") {
                sut().location.value.shouldBe(State.Loading)
            }
        }

        context("when the locator emits a location") {
            it("becomes loaded with the emitted location") {
                val locator = TestLocator()
                val expectedLocation = randomLocation()
                sut(locator = locator).location.test {
                    awaitItem() // Loading state

                    locator.setLocation(expectedLocation)

                    val state = awaitItem()
                    state.shouldBeInstanceOf<State.Loaded<Location>>()
                    state.data.shouldBe(expectedLocation)
                }
            }
        }

        context("when the location changes") {
            it("is updated with the new location") {
                val locator = TestLocator()
                val subject = sut(locator = locator)
                val expectedLocation = randomLocation()
                subject.location.test {
                    awaitItem() // Loading state
                    locator.setLocation(randomLocation())
                    awaitItem() // Initial data state

                    locator.setLocation(expectedLocation)

                    val state = awaitItem()
                    state.shouldBeInstanceOf<State.Loaded<Location>>()
                    state.data.shouldBe(expectedLocation)
                }
            }
        }
    }

    describe("the settings state") {
        context("just after initialization") {
            it("is loading") {
                sut().settings.value.shouldBe(State.Loading)
            }
        }

        context("when all settings are emitted") {
            it("becomes loaded with the correct values") {
                val settings = TestSettingsRepository()
                val subject = sut(settings = settings)
                val expectedUnits = randomUnits()
                val expectedCoordinatesFormat = randomCoordinatesFormat()
                val expectedLocationAccuracyVisibility = randomLocationAccuracyVisibility()
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
        }

        context("when the coordinates format changes") {
            it("is updated with the new coordinates format") {
                val settings = TestSettingsRepository()
                val subject = sut(settings = settings)
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
        }

        context("when the units change") {
            it("is updated with the new units") {
                val settings = TestSettingsRepository()
                val subject = sut(settings = settings)
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
        }

        context("when the location accuracy visibility changes") {
            it("is updated with the new visibility") {
                val settings = TestSettingsRepository()
                val subject = sut(settings = settings)
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
    }
})
