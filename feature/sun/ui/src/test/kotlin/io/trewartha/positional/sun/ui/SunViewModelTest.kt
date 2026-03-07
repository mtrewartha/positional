package io.trewartha.positional.sun.ui

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.test.FakeClock
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.location.Location
import io.trewartha.positional.location.TestLocator
import io.trewartha.positional.sun.TestSolarTimesRepository
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Clock

class SunViewModelTest : DescribeSpec({

    val jan1st = LocalDate(2024, Month.JANUARY, 1)

    fun sut(
        clock: Clock = FakeClock(jan1st.atStartOfDayIn(TimeZone.currentSystemDefault())),
        locator: TestLocator = TestLocator(),
        solarTimesRepository: TestSolarTimesRepository = TestSolarTimesRepository(),
    ): SunViewModel = SunViewModel(clock, locator, solarTimesRepository)

    describe("today's date") {
        context("just after initialization") {
            it("is the current date") {
                sut().state.value.todaysDate.shouldBe(jan1st)
            }
        }
    }

    describe("the selected date") {
        context("just after initialization") {
            it("matches today's date") {
                val subject = sut()
                subject.state.value.selectedDate.shouldBe(subject.state.value.todaysDate)
            }
        }

        context("when the user selects a new date") {
            it("is updated to the new date") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                val expectedSelectedDate = jan1st + DatePeriod(days = 1)
                subject.state.test {
                    awaitItem() // Ignore the current state
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunrise(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunset(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setNauticalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    awaitItem()

                    subject.onSelectedDateChange(expectedSelectedDate)

                    awaitItem().selectedDate.shouldBe(expectedSelectedDate)
                }
            }
        }

        context("when the user decrements the date") {
            it("moves back by one day") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                subject.state.test {
                    awaitItem() // Ignore the current state
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunrise(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunset(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setNauticalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    awaitItem()

                    subject.onSelectedDateDecrement()

                    awaitItem().selectedDate.shouldBe(jan1st - DatePeriod(days = 1))
                }
            }
        }

        context("when the user increments the date") {
            it("moves forward by one day") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                subject.state.test {
                    awaitItem() // Ignore the current state
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunrise(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunset(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setNauticalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    awaitItem()

                    subject.onSelectedDateIncrement()

                    awaitItem().selectedDate.shouldBe(jan1st + DatePeriod(days = 1))
                }
            }
        }

        context("when the user resets to today after navigating to a different date") {
            it("is updated to today") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                subject.state.test {
                    awaitItem() // Ignore the current state
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDawn(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunrise(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setSunset(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setCivilDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setNauticalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan1st,
                        LocalTime(0, 0, 0)
                    )
                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    awaitItem()

                    val differentDate = jan1st + DatePeriod(days = 5)
                    subject.onSelectedDateChange(differentDate)
                    awaitItem()

                    subject.onSelectedDateChangedToToday()

                    awaitItem().selectedDate.shouldBe(jan1st)
                }
            }
        }
    }

    describe("the solar times") {
        context("just after initialization") {
            it("each time is in the loading state") {
                val subject = sut()
                val times = with(subject.state.value) {
                    listOf(
                        astronomicalDawn,
                        nauticalDawn,
                        civilDawn,
                        sunrise,
                        sunset,
                        civilDusk,
                        nauticalDusk,
                        astronomicalDusk
                    )
                }
                for (time in times) time.shouldBeInstanceOf<State.Loading>()
            }
        }

        context("when the locator emits a location") {
            it("are updated for the location and current date") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                val expectedAstronomicalDawn = LocalTime(0, 0, 1)
                val expectedNauticalDawn = LocalTime(0, 0, 2)
                val expectedCivilDawn = LocalTime(0, 0, 3)
                val expectedSunrise = LocalTime(0, 0, 4)
                val expectedSunset = LocalTime(0, 0, 5)
                val expectedCivilDusk = LocalTime(0, 0, 6)
                val expectedNauticalDusk = LocalTime(0, 0, 7)
                val expectedAstronomicalDusk = LocalTime(0, 0, 8)
                subject.state.test {
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan1st,
                        expectedAstronomicalDawn
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan1st, expectedNauticalDawn)
                    solarTimesRepository.setCivilDawn(coordinates, jan1st, expectedCivilDawn)
                    solarTimesRepository.setSunrise(coordinates, jan1st, expectedSunrise)
                    solarTimesRepository.setSunset(coordinates, jan1st, expectedSunset)
                    solarTimesRepository.setCivilDusk(coordinates, jan1st, expectedCivilDusk)
                    solarTimesRepository.setNauticalDusk(coordinates, jan1st, expectedNauticalDusk)
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan1st,
                        expectedAstronomicalDusk
                    )
                    awaitItem() // Ignore the current state

                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    val result = awaitItem()

                    result.todaysDate.shouldBe(jan1st)
                    result.selectedDate.shouldBe(jan1st)
                    result.astronomicalDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedAstronomicalDawn)
                    result.nauticalDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedNauticalDawn)
                    result.civilDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedCivilDawn)
                    result.sunrise.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedSunrise)
                    result.sunset.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedSunset)
                    result.civilDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedCivilDusk)
                    result.nauticalDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedNauticalDusk)
                    result.astronomicalDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedAstronomicalDusk)
                }
            }
        }

        context("when the user selects a new date with solar times available") {
            it("are updated for the new date") {
                val locator = TestLocator()
                val solarTimesRepository = TestSolarTimesRepository()
                val subject = sut(locator = locator, solarTimesRepository = solarTimesRepository)
                val coordinates = GeodeticCoordinates(0.degrees, 0.degrees)
                val jan2nd = jan1st + DatePeriod(days = 1)
                val expectedAstronomicalDawn = LocalTime(0, 0, 1)
                val expectedNauticalDawn = LocalTime(0, 0, 2)
                val expectedCivilDawn = LocalTime(0, 0, 3)
                val expectedSunrise = LocalTime(0, 0, 4)
                val expectedSunset = LocalTime(0, 0, 5)
                val expectedCivilDusk = LocalTime(0, 0, 6)
                val expectedNauticalDusk = LocalTime(0, 0, 7)
                val expectedAstronomicalDusk = LocalTime(0, 0, 8)
                subject.state.test {
                    awaitItem() // Ignore the current state
                    solarTimesRepository.setAstronomicalDawn(
                        coordinates,
                        jan2nd,
                        expectedAstronomicalDawn
                    )
                    solarTimesRepository.setNauticalDawn(coordinates, jan2nd, expectedNauticalDawn)
                    solarTimesRepository.setCivilDawn(coordinates, jan2nd, expectedCivilDawn)
                    solarTimesRepository.setSunrise(coordinates, jan2nd, expectedSunrise)
                    solarTimesRepository.setSunset(coordinates, jan2nd, expectedSunset)
                    solarTimesRepository.setCivilDusk(coordinates, jan2nd, expectedCivilDusk)
                    solarTimesRepository.setNauticalDusk(coordinates, jan2nd, expectedNauticalDusk)
                    solarTimesRepository.setAstronomicalDusk(
                        coordinates,
                        jan2nd,
                        expectedAstronomicalDusk
                    )
                    locator.setLocation(
                        Location(
                            jan1st.atStartOfDayIn(TimeZone.currentSystemDefault()),
                            coordinates
                        )
                    )
                    awaitItem()

                    subject.onSelectedDateChange(jan2nd)
                    val result = awaitItem()

                    result.astronomicalDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedAstronomicalDawn)
                    result.nauticalDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedNauticalDawn)
                    result.civilDawn.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedCivilDawn)
                    result.sunrise.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedSunrise)
                    result.sunset.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedSunset)
                    result.civilDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedCivilDusk)
                    result.nauticalDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedNauticalDusk)
                    result.astronomicalDusk.shouldBeInstanceOf<State.Loaded<LocalTime?>>()
                        .dataOrNull.shouldBe(expectedAstronomicalDusk)
                }
            }
        }
    }
})
