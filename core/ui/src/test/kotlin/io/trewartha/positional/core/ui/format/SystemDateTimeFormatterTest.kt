package io.trewartha.positional.core.ui.format

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import io.trewartha.positional.core.test.FakeClock
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

// Java 21 CLDR data uses U+202F (narrow no-break space) before AM/PM indicators in en-US locale
private const val NNBSP = ' '

class SystemDateTimeFormatterTest : DescribeSpec({

    fun sut(
        clock: Clock = FakeClock(Instant.fromEpochMilliseconds(0)),
        locale: Locale = Locale.US,
        timeZone: TimeZone = TimeZone.UTC,
    ): SystemDateTimeFormatter = SystemDateTimeFormatter(clock, locale, timeZone)

    describe("formatting a date") {
        it("returns the date in medium format") {
            sut().formatDate(LocalDate(2024, 1, 15)).shouldBe("Jan 15, 2024")
        }

        it("returns the same calendar date regardless of the time zone") {
            checkAll(Exhaustive.collection(timeZones)) { timeZone ->
                sut(timeZone = timeZone).formatDate(LocalDate(2024, 1, 15))
                    .shouldBe("Jan 15, 2024")
            }
        }

        it("follows the locale") {
            sut(locale = Locale.GERMANY).formatDate(LocalDate(2024, 1, 15))
                .shouldBe("15.01.2024")
        }
    }

    describe("formatting a date and time") {
        it("returns the date and time in medium date and short time format") {
            sut().formatDateTime(LocalDateTime(2024, 1, 15, 11, 22, 0))
                .shouldBe("Jan 15, 2024, 11:22${NNBSP}AM")
        }

        it("returns the same wall-clock date and time regardless of the time zone") {
            checkAll(Exhaustive.collection(timeZones)) { timeZone ->
                sut(timeZone = timeZone).formatDateTime(LocalDateTime(2024, 1, 15, 23, 59, 0))
                    .shouldBe("Jan 15, 2024, 11:59${NNBSP}PM")
            }
        }

        it("follows the locale") {
            sut(locale = Locale.GERMANY).formatDateTime(LocalDateTime(2024, 1, 15, 11, 22, 0))
                .shouldBe("15.01.2024, 11:22")
        }
    }

    describe("formatting the full day of the week") {
        it("returns the full English name of the day") {
            checkAll(
                Exhaustive.collection(
                    listOf(
                        LocalDate(2024, 1, 15) to "Monday",
                        LocalDate(2024, 1, 16) to "Tuesday",
                        LocalDate(2024, 1, 17) to "Wednesday",
                        LocalDate(2024, 1, 18) to "Thursday",
                        LocalDate(2024, 1, 19) to "Friday",
                        LocalDate(2024, 1, 20) to "Saturday",
                        LocalDate(2024, 1, 21) to "Sunday",
                    )
                )
            ) { (date, expectedDayName) ->
                sut().formatFullDayOfWeek(date).shouldBe(expectedDayName)
            }
        }

        it("returns the same day regardless of the time zone") {
            checkAll(Exhaustive.collection(timeZones)) { timeZone ->
                sut(timeZone = timeZone).formatFullDayOfWeek(LocalDate(2024, 1, 15))
                    .shouldBe("Monday")
            }
        }

        it("follows the locale") {
            sut(locale = Locale.GERMANY).formatFullDayOfWeek(LocalDate(2024, 1, 15))
                .shouldBe("Montag")
        }
    }

    describe("formatting a time") {
        context("when seconds are not requested") {
            it("omits seconds from the result") {
                sut().formatTime(LocalTime(11, 22, 33), includeSeconds = false)
                    .shouldBe("11:22${NNBSP}AM")
            }

            it("omits seconds by default") {
                sut().formatTime(LocalTime(11, 22, 33)).shouldBe("11:22${NNBSP}AM")
            }
        }

        context("when seconds are requested") {
            it("includes seconds in the result") {
                sut().formatTime(LocalTime(11, 22, 33), includeSeconds = true)
                    .shouldBe("11:22:33${NNBSP}AM")
            }
        }

        it("formats midnight as 12 AM") {
            sut().formatTime(LocalTime(0, 0, 0)).shouldBe("12:00${NNBSP}AM")
        }

        it("formats noon as 12 PM") {
            sut().formatTime(LocalTime(12, 0, 0)).shouldBe("12:00${NNBSP}PM")
        }

        it("formats the last minute of the day as 11:59 PM") {
            sut().formatTime(LocalTime(23, 59, 59), includeSeconds = true)
                .shouldBe("11:59:59${NNBSP}PM")
        }

        it("returns the same wall-clock time regardless of the time zone") {
            checkAll(Exhaustive.collection(timeZones)) { timeZone ->
                sut(timeZone = timeZone).formatTime(LocalTime(11, 22, 33), includeSeconds = true)
                    .shouldBe("11:22:33${NNBSP}AM")
            }
        }

        it("returns the same wall-clock time regardless of the clock's current date") {
            val daylightSavingsTransition = Instant.parse("2024-03-10T12:00:00Z")
            sut(
                clock = FakeClock(daylightSavingsTransition),
                timeZone = TimeZone.of("America/New_York"),
            ).formatTime(LocalTime(11, 22, 33)).shouldBe("11:22${NNBSP}AM")
        }

        it("follows the locale") {
            sut(locale = Locale.GERMANY).formatTime(LocalTime(15, 22, 33), includeSeconds = true)
                .shouldBe("15:22:33")
        }
    }
})

private val timeZones = listOf(
    TimeZone.UTC,
    TimeZone.of("Asia/Tokyo"),
    TimeZone.of("America/Los_Angeles"),
    TimeZone.of("Pacific/Kiritimati"),
    TimeZone.of("Pacific/Pago_Pago"),
)
