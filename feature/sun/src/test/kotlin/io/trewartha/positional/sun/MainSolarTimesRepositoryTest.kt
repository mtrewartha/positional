package io.trewartha.positional.sun

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import java.util.TimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

class MainSolarTimesRepositoryTest : DescribeSpec({

    fun sut() = MainSolarTimesRepository()

    describe("getting astronomical dawn") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getAstronomicalDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(4, 53))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getAstronomicalDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting nautical dawn") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getNauticalDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(5, 19))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getNauticalDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting civil dawn") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getCivilDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(5, 46))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getCivilDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting sunrise") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getSunrise(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(6, 8))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getSunrise(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting sunset") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getSunset(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(18, 16))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getSunset(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting civil dusk") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getCivilDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(18, 38))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getCivilDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting nautical dusk") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getNauticalDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(19, 4))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getNauticalDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }

    describe("getting astronomical dusk") {
        context("for a location near the equator on the winter solstice") {
            it("returns the correct time") {
                val originalTimeZone = TimeZone.getDefault()
                val mitadDelMundo = GeodeticCoordinates((-0.00217).degrees, (-78.45581).degrees)
                val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(mitadDelMundoTimeZone)
                try {
                    sut().getAstronomicalDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(19, 31))
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }

        context("for a location near the north pole on the winter solstice") {
            it("returns null") {
                val originalTimeZone = TimeZone.getDefault()
                val northOfLongyearbyen = GeodeticCoordinates(89.degrees, 15.490.degrees)
                val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
                val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
                TimeZone.setDefault(longyearbyenTimeZone)
                try {
                    sut().getAstronomicalDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
                } finally {
                    TimeZone.setDefault(originalTimeZone)
                }
            }
        }
    }
})
