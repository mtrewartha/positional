package io.trewartha.positional.domain.solunar

import io.trewartha.positional.data.solunar.SolarTimes
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * @see invoke
 */
class GetSolarTimesUseCase @Inject constructor(
    private val solarTimesRepository: SolarTimesRepository
) {

    /**
     * Gets the [solar times][SolarTimes] for a given location and date
     *
     * @param localDate Date that's local to the given [latitude] and [longitude]
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     *
     * @return The [solar times][SolarTimes] on the given date at the given location
     */
    operator fun invoke(localDate: LocalDate, latitude: Double, longitude: Double): SolarTimes =
        SolarTimes(
            astronomicalDawn = solarTimesRepository.getAstronomicalDawn(
                localDate,
                latitude,
                longitude
            ),
            nauticalDawn = solarTimesRepository.getNauticalDawn(localDate, latitude, longitude),
            civilDawn = solarTimesRepository.getCivilDawn(localDate, latitude, longitude),
            sunrise = solarTimesRepository.getSunrise(localDate, latitude, longitude),
            sunset = solarTimesRepository.getSunset(localDate, latitude, longitude),
            civilDusk = solarTimesRepository.getCivilDusk(localDate, latitude, longitude),
            nauticalDusk = solarTimesRepository.getNauticalDusk(localDate, latitude, longitude),
            astronomicalDusk = solarTimesRepository.getAstronomicalDusk(
                localDate,
                latitude,
                longitude
            ),
        )
}
