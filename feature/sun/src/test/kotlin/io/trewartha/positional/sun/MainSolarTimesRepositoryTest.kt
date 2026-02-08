package io.trewartha.positional.sun

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import java.util.TimeZone

class MainSolarTimesRepositoryTest : AnnotationSpec() {

    private val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
    private val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
    private val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
    private val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
    private val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
    private lateinit var originalDefaultTimeZone: TimeZone

    private lateinit var subject: SolarTimesRepository

    @BeforeEach
    fun setUp() {
        originalDefaultTimeZone = TimeZone.getDefault()
        subject = MainSolarTimesRepository()
    }

    @AfterEach
    fun tearDown() {
        TimeZone.setDefault(originalDefaultTimeZone)
    }

    @Test
    fun `Getting astronomical dawn returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getAstronomicalDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(4, 53))
    }

    @Test
    fun `Getting astronomical dawn when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getAstronomicalDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting nautical dawn returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getNauticalDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(5, 19))
    }

    @Test
    fun `Getting nautical dawn when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getNauticalDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting civil dawn returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getCivilDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(5, 46))
    }

    @Test
    fun `Getting civil dawn when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getCivilDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting sunrise returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getSunrise(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(6, 8))
    }

    @Test
    fun `Getting sunrise when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getSunrise(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting sunset returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getSunset(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(18, 16))
    }

    @Test
    fun `Getting sunset when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getSunset(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting civil dusk returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getCivilDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(18, 38))
    }

    @Test
    fun `Getting civil dusk when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getCivilDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting nautical dusk returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getNauticalDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(19, 4))
    }

    @Test
    fun `Getting nautical dusk when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getNauticalDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun `Getting astronomical dusk returns correct time`() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getAstronomicalDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(19, 31))
    }

    @Test
    fun `Getting astronomical dusk when there is none returns null`() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getAstronomicalDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }
}
