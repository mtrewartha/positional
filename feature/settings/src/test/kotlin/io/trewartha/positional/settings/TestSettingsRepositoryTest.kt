package io.trewartha.positional.settings

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.firstOrNull

class TestSettingsRepositoryTest : DescribeSpec({

    fun sut() = TestSettingsRepository()

    describe("the compass mode flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstCompassMode = randomCompassMode()
                val secondCompassMode = randomCompassMode()
                subject.setCompassMode(firstCompassMode)
                subject.setCompassMode(secondCompassMode)
                subject.compassMode.firstOrNull().shouldBe(secondCompassMode)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.compassMode.test {
                    val compassMode = randomCompassMode()
                    subject.setCompassMode(compassMode)
                    awaitItem().shouldBe(compassMode)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.compassMode.test {
                    val compassMode = randomCompassMode()
                    subject.setCompassMode(compassMode)
                    awaitItem().shouldBe(compassMode)
                    subject.setCompassMode(compassMode)
                    expectNoEvents()
                }
            }
        }
    }

    describe("the compass north vibration flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstCompassNorthVibration = randomCompassNorthVibration()
                val secondCompassNorthVibration = randomCompassNorthVibration()
                subject.setCompassNorthVibration(firstCompassNorthVibration)
                subject.setCompassNorthVibration(secondCompassNorthVibration)
                subject.compassNorthVibration.firstOrNull().shouldBe(secondCompassNorthVibration)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.compassNorthVibration.test {
                    val compassNorthVibration = randomCompassNorthVibration()
                    subject.setCompassNorthVibration(compassNorthVibration)
                    awaitItem().shouldBe(compassNorthVibration)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.compassNorthVibration.test {
                    val compassNorthVibration = randomCompassNorthVibration()
                    subject.setCompassNorthVibration(compassNorthVibration)
                    awaitItem().shouldBe(compassNorthVibration)
                    subject.setCompassNorthVibration(compassNorthVibration)
                    expectNoEvents()
                }
            }
        }
    }

    describe("the coordinates format flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstCoordinatesFormat = randomCoordinatesFormat()
                val secondCoordinatesFormat = randomCoordinatesFormat()
                subject.setCoordinatesFormat(firstCoordinatesFormat)
                subject.setCoordinatesFormat(secondCoordinatesFormat)
                subject.coordinatesFormat.firstOrNull().shouldBe(secondCoordinatesFormat)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.coordinatesFormat.test {
                    val coordinatesFormat = randomCoordinatesFormat()
                    subject.setCoordinatesFormat(coordinatesFormat)
                    awaitItem().shouldBe(coordinatesFormat)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.coordinatesFormat.test {
                    val coordinatesFormat = randomCoordinatesFormat()
                    subject.setCoordinatesFormat(coordinatesFormat)
                    awaitItem().shouldBe(coordinatesFormat)
                    subject.setCoordinatesFormat(coordinatesFormat)
                    expectNoEvents()
                }
            }
        }
    }

    describe("the location accuracy visibility flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstVisibility = randomLocationAccuracyVisibility()
                val secondVisibility = randomLocationAccuracyVisibility()
                subject.setLocationAccuracyVisibility(firstVisibility)
                subject.setLocationAccuracyVisibility(secondVisibility)
                subject.locationAccuracyVisibility.firstOrNull().shouldBe(secondVisibility)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.locationAccuracyVisibility.test {
                    val visibility = randomLocationAccuracyVisibility()
                    subject.setLocationAccuracyVisibility(visibility)
                    awaitItem().shouldBe(visibility)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.locationAccuracyVisibility.test {
                    val visibility = randomLocationAccuracyVisibility()
                    subject.setLocationAccuracyVisibility(visibility)
                    awaitItem().shouldBe(visibility)
                    subject.setLocationAccuracyVisibility(visibility)
                    expectNoEvents()
                }
            }
        }
    }

    describe("the theme flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstTheme = randomTheme()
                val secondTheme = randomTheme()
                subject.setTheme(firstTheme)
                subject.setTheme(secondTheme)
                subject.theme.firstOrNull().shouldBe(secondTheme)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.theme.test {
                    val theme = randomTheme()
                    subject.setTheme(theme)
                    awaitItem().shouldBe(theme)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.theme.test {
                    val theme = randomTheme()
                    subject.setTheme(theme)
                    awaitItem().shouldBe(theme)
                    subject.setTheme(theme)
                    expectNoEvents()
                }
            }
        }
    }

    describe("the units flow") {
        context("when two values are set in sequence") {
            it("only the last set value is emitted") {
                val subject = sut()
                val firstUnits = randomUnits()
                val secondUnits = randomUnits()
                subject.setUnits(firstUnits)
                subject.setUnits(secondUnits)
                subject.units.firstOrNull().shouldBe(secondUnits)
            }
        }

        context("when a value is set") {
            it("emits the set value") {
                val subject = sut()
                subject.units.test {
                    val units = randomUnits()
                    subject.setUnits(units)
                    awaitItem().shouldBe(units)
                }
            }
        }

        context("when the same value is set twice") {
            it("only emits once") {
                val subject = sut()
                subject.units.test {
                    val units = randomUnits()
                    subject.setUnits(units)
                    awaitItem().shouldBe(units)
                    subject.setUnits(units)
                    expectNoEvents()
                }
            }
        }
    }
})
