package io.trewartha.positional.ui.sun

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.trewartha.positional.data.location.TestLocator
import io.trewartha.positional.data.sun.TestSolarTimesRepository
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.ui.core.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SunViewModelTest {

    private val timeZone = TimeZone.currentSystemDefault()
    private val jan1st = LocalDate(2024, Month.JANUARY, 1)

    private lateinit var clock: Clock
    private lateinit var locator: TestLocator
    private lateinit var solarTimesRepository: TestSolarTimesRepository
    private lateinit var subject: SunViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        clock = object : Clock {
            override fun now(): Instant = jan1st.atStartOfDayIn(timeZone)
        }
        locator = TestLocator()
        solarTimesRepository = TestSolarTimesRepository()

        subject = SunViewModel(clock, locator, solarTimesRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial state's date for today is correct`() {
        subject.state.value.todaysDate.shouldBe(LocalDate(2024, Month.JANUARY, 1))
    }

    @Test
    fun `Initial state's selected day is today`() {
        subject.state.value.selectedDate.shouldBe(subject.state.value.todaysDate)
    }

    @Test
    fun `Initial state indicates each time is loading`() {
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

    @Test
    fun `Selected date updates when user selects new date`() = runTest {
        subject.state.test {
            awaitItem() // Ignore the current state
            val coordinates = Coordinates(0.0, 0.0)
            solarTimesRepository.setAstronomicalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setNauticalDawn(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setCivilDawn(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setSunrise(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setSunset(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setCivilDusk(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setNauticalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
            solarTimesRepository.setAstronomicalDusk(coordinates, jan1st, LocalTime(0, 0, 0))
            locator.setLocation(Location(clock.now(), coordinates))
            awaitItem()

            val expectedSelectedDate = jan1st + DatePeriod(days = 1)
            subject.onSelectedDateChange(expectedSelectedDate)

            awaitItem().selectedDate.shouldBe(expectedSelectedDate)
        }
    }

    @Test
    fun `Times updates when user selects new date`() = runTest {
        subject.state.test {
            awaitItem() // Ignore the current state
            val coordinates = Coordinates(0.0, 0.0)
            val expectedAstronomicalDawn = LocalTime(0, 0, 1)
            val expectedNauticalDawn = LocalTime(0, 0, 2)
            val expectedCivilDawn = LocalTime(0, 0, 3)
            val expectedSunrise = LocalTime(0, 0, 4)
            val expectedSunset = LocalTime(0, 0, 5)
            val expectedCivilDusk = LocalTime(0, 0, 6)
            val expectedNauticalDusk = LocalTime(0, 0, 7)
            val expectedAstronomicalDusk = LocalTime(0, 0, 8)
            val jan2nd = jan1st + DatePeriod(days = 1)
            solarTimesRepository.setAstronomicalDawn(coordinates, jan2nd, expectedAstronomicalDawn)
            solarTimesRepository.setNauticalDawn(coordinates, jan2nd, expectedNauticalDawn)
            solarTimesRepository.setCivilDawn(coordinates, jan2nd, expectedCivilDawn)
            solarTimesRepository.setSunrise(coordinates, jan2nd, expectedSunrise)
            solarTimesRepository.setSunset(coordinates, jan2nd, expectedSunset)
            solarTimesRepository.setCivilDusk(coordinates, jan2nd, expectedCivilDusk)
            solarTimesRepository.setNauticalDusk(coordinates, jan2nd, expectedNauticalDusk)
            solarTimesRepository.setAstronomicalDusk(coordinates, jan2nd, expectedAstronomicalDusk)
            locator.setLocation(Location(clock.now(), coordinates))
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

    @Test
    fun `Times updates when location changes`() = runTest {
        subject.state.test {
            val coordinates = Coordinates(0.0, 0.0)
            val expectedAstronomicalDawn = LocalTime(0, 0, 1)
            val expectedNauticalDawn = LocalTime(0, 0, 2)
            val expectedCivilDawn = LocalTime(0, 0, 3)
            val expectedSunrise = LocalTime(0, 0, 4)
            val expectedSunset = LocalTime(0, 0, 5)
            val expectedCivilDusk = LocalTime(0, 0, 6)
            val expectedNauticalDusk = LocalTime(0, 0, 7)
            val expectedAstronomicalDusk = LocalTime(0, 0, 8)
            solarTimesRepository.setAstronomicalDawn(coordinates, jan1st, expectedAstronomicalDawn)
            solarTimesRepository.setNauticalDawn(coordinates, jan1st, expectedNauticalDawn)
            solarTimesRepository.setCivilDawn(coordinates, jan1st, expectedCivilDawn)
            solarTimesRepository.setSunrise(coordinates, jan1st, expectedSunrise)
            solarTimesRepository.setSunset(coordinates, jan1st, expectedSunset)
            solarTimesRepository.setCivilDusk(coordinates, jan1st, expectedCivilDusk)
            solarTimesRepository.setNauticalDusk(coordinates, jan1st, expectedNauticalDusk)
            solarTimesRepository.setAstronomicalDusk(coordinates, jan1st, expectedAstronomicalDusk)
            awaitItem() // Ignore the current state

            locator.setLocation(Location(clock.now(), coordinates))
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
