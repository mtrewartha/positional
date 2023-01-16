package io.trewartha.positional.domain.twilight

import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.datetime.LocalDate
import java.time.Month

class GetDailyTwilightsUseCaseSpec : BehaviorSpec({

    lateinit var subject: GetDailyTwilightsUseCase
    lateinit var fakeTwilightRepository: FakeTwilightRepository

    beforeEach {
        fakeTwilightRepository = FakeTwilightRepository()
        subject = GetDailyTwilightsUseCase(fakeTwilightRepository)
    }

    given("a date, latitude, and longitude") {
        val date = LocalDate(2000, Month.JANUARY, 1)
        val latitude = 0.0f
        val longitude = 0.0f
        `when`("the use case is executed") {

            then("solar times are returned") {

            }
        }
    }
})
