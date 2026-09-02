package io.trewartha.positional.core.ui.format

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
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
    }

    describe("formatting a date and time") {
        it("returns the date and time in medium date and short time format") {
            sut().formatDateTime(LocalDateTime(2024, 1, 15, 11, 22, 0))
                .shouldBe("Jan 15, 2024, 11:22${NNBSP}AM")
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
    }

    describe("formatting a time") {
        context("when seconds are not requested") {
            it("omits seconds from the result") {
                sut().formatTime(LocalTime(11, 22, 33), includeSeconds = false)
                    .shouldNotContain(":33")
            }
        }

        context("when seconds are requested") {
            it("includes seconds in the result") {
                sut().formatTime(LocalTime(11, 22, 33), includeSeconds = true)
                    .shouldContain(":33")
            }
        }
    }
})
