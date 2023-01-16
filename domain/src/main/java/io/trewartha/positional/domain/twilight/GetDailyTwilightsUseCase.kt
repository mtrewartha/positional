package io.trewartha.positional.domain.twilight

import io.trewartha.positional.data.twilight.DailyTwilights
import io.trewartha.positional.data.twilight.Twilights
import kotlinx.datetime.LocalDate

/**
 * @see invoke
 */
class GetDailyTwilightsUseCase(
    private val twilightRepository: TwilightRepository
) {

    /**
     * Gets the [daily twilights][DailyTwilights] times for a given location and date
     *
     * @param date Date that's local to the given [latitude] and [longitude]
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     *
     * @return The [daily twilight][DailyTwilights] on the given date at the given location
     */
    operator fun invoke(date: LocalDate, latitude: Double, longitude: Double): DailyTwilights =
        DailyTwilights(
            morningTwilights = Twilights(
                horizonTwilight = twilightRepository.getSunrise(date, latitude, longitude),
                civilTwilight = twilightRepository
                    .getMorningCivilTwilight(date, latitude, longitude),
                nauticalTwilight = twilightRepository
                    .getMorningNauticalTwilight(date, latitude, longitude),
                astronomicalTwilight = twilightRepository
                    .getMorningAstronomicalTwilight(date, latitude, longitude),
            ),
            Twilights(
                horizonTwilight = twilightRepository.getSunset(date, latitude, longitude),
                civilTwilight = twilightRepository
                    .getEveningCivilTwilight(date, latitude, longitude),
                nauticalTwilight = twilightRepository
                    .getEveningNauticalTwilight(date, latitude, longitude),
                astronomicalTwilight = twilightRepository
                    .getEveningAstronomicalTwilight(date, latitude, longitude),
            )
        )
}
