package io.trewartha.positional.data.sun

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestSolarTimesRepositoryTest {

    private lateinit var subject: TestSolarTimesRepository

    private val coordinates = Coordinates(0.0, 0.0)
    private val date = LocalDate(2000, Month.JANUARY, 1)
    private val expectedValue = LocalTime(1, 2, 3)

    @BeforeTest
    fun setUp() {
        subject = TestSolarTimesRepository()
    }

    @Test
    fun `Getting sunrise returns one if present`() {
        subject.setSunrise(coordinates, date, expectedValue)

        subject.getSunrise(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting sunrise returns null if not present`() {
        subject.setSunrise(coordinates, date, null)

        subject.getSunrise(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting sunset returns one if present`() {
        subject.setSunset(coordinates, date, expectedValue)

        subject.getSunset(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting sunset returns null if not present`() {
        subject.setSunset(coordinates, date, null)

        subject.getSunset(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting civil dawn returns one if present`() {
        subject.setCivilDawn(coordinates, date, expectedValue)

        subject.getCivilDawn(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting civil dawn returns null if not present`() {
        subject.setCivilDawn(coordinates, date, null)

        subject.getCivilDawn(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting civil dusk returns one if present`() {
        subject.setCivilDusk(coordinates, date, expectedValue)

        subject.getCivilDusk(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting civil dusk returns null if not present`() {
        subject.setCivilDusk(coordinates, date, null)

        subject.getCivilDusk(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting nautical dawn returns one if present`() {
        subject.setNauticalDawn(coordinates, date, expectedValue)

        subject.getNauticalDawn(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting nautical dawn returns null if not present`() {
        subject.setNauticalDawn(coordinates, date, null)

        subject.getNauticalDawn(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting nautical dusk returns one if present`() {
        subject.setNauticalDusk(coordinates, date, expectedValue)

        subject.getNauticalDusk(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting nautical dusk returns null if not present`() {
        subject.setNauticalDusk(coordinates, date, null)

        subject.getNauticalDusk(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting astronomical dawn returns one if present`() {
        subject.setAstronomicalDawn(coordinates, date, expectedValue)

        subject.getAstronomicalDawn(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting astronomical dawn returns null if not present`() {
        subject.setAstronomicalDawn(coordinates, date, null)

        subject.getAstronomicalDawn(coordinates, date).shouldBeNull()
    }

    @Test
    fun `Getting astronomical dusk returns one if present`() {
        subject.setAstronomicalDusk(coordinates, date, expectedValue)

        subject.getAstronomicalDusk(coordinates, date).shouldBe(expectedValue)
    }

    @Test
    fun `Getting astronomical dusk returns null if not present`() {
        subject.setAstronomicalDusk(coordinates, date, null)

        subject.getAstronomicalDusk(coordinates, date).shouldBeNull()
    }
}
