package io.trewartha.positional.sun

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.randomGeodeticCoordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

class TestSolarTimesRepositoryTest : DescribeSpec({

    fun sut() = TestSolarTimesRepository()

    describe("getting sunrise") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setSunrise(coordinates, date, expectedValue)
                subject.getSunrise(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setSunrise(coordinates, date, null)
                subject.getSunrise(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getSunrise(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setSunrise(coordinates, date, initialValue)
                subject.setSunrise(coordinates, date, newValue)
                subject.getSunrise(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting sunset") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setSunset(coordinates, date, expectedValue)
                subject.getSunset(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setSunset(coordinates, date, null)
                subject.getSunset(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getSunset(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setSunset(coordinates, date, initialValue)
                subject.setSunset(coordinates, date, newValue)
                subject.getSunset(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting civil dawn") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setCivilDawn(coordinates, date, expectedValue)
                subject.getCivilDawn(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setCivilDawn(coordinates, date, null)
                subject.getCivilDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getCivilDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setCivilDawn(coordinates, date, initialValue)
                subject.setCivilDawn(coordinates, date, newValue)
                subject.getCivilDawn(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting civil dusk") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setCivilDusk(coordinates, date, expectedValue)
                subject.getCivilDusk(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setCivilDusk(coordinates, date, null)
                subject.getCivilDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getCivilDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setCivilDusk(coordinates, date, initialValue)
                subject.setCivilDusk(coordinates, date, newValue)
                subject.getCivilDusk(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting nautical dawn") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setNauticalDawn(coordinates, date, expectedValue)
                subject.getNauticalDawn(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setNauticalDawn(coordinates, date, null)
                subject.getNauticalDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getNauticalDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setNauticalDawn(coordinates, date, initialValue)
                subject.setNauticalDawn(coordinates, date, newValue)
                subject.getNauticalDawn(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting nautical dusk") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setNauticalDusk(coordinates, date, expectedValue)
                subject.getNauticalDusk(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setNauticalDusk(coordinates, date, null)
                subject.getNauticalDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getNauticalDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setNauticalDusk(coordinates, date, initialValue)
                subject.setNauticalDusk(coordinates, date, newValue)
                subject.getNauticalDusk(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting astronomical dawn") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setAstronomicalDawn(coordinates, date, expectedValue)
                subject.getAstronomicalDawn(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setAstronomicalDawn(coordinates, date, null)
                subject.getAstronomicalDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getAstronomicalDawn(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setAstronomicalDawn(coordinates, date, initialValue)
                subject.setAstronomicalDawn(coordinates, date, newValue)
                subject.getAstronomicalDawn(coordinates, date).shouldBe(newValue)
            }
        }
    }

    describe("getting astronomical dusk") {
        context("when a non-null value has been set") {
            it("returns the set value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val expectedValue = LocalTime(1, 2, 3)
                subject.setAstronomicalDusk(coordinates, date, expectedValue)
                subject.getAstronomicalDusk(coordinates, date).shouldBe(expectedValue)
            }
        }

        context("when a null value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.setAstronomicalDusk(coordinates, date, null)
                subject.getAstronomicalDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when no value has been set") {
            it("returns null") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                subject.getAstronomicalDusk(coordinates, date).shouldBeNull()
            }
        }

        context("when a value has been overwritten with a new value") {
            it("returns the new value") {
                val subject = sut()
                val coordinates = randomGeodeticCoordinates()
                val date = LocalDate(2000, Month.JANUARY, 1)
                val initialValue = LocalTime(1, 2, 3)
                val newValue = LocalTime(4, 5, 6)
                subject.setAstronomicalDusk(coordinates, date, initialValue)
                subject.setAstronomicalDusk(coordinates, date, newValue)
                subject.getAstronomicalDusk(coordinates, date).shouldBe(newValue)
            }
        }
    }
})
