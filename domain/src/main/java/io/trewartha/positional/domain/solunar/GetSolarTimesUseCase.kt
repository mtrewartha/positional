package io.trewartha.positional.domain.solunar

import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.solunar.SolarTimes
import io.trewartha.positional.data.solunar.SolarTimesRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * @see invoke
 */
class GetSolarTimesUseCase @Inject constructor(
    private val solarTimesRepository: SolarTimesRepository
) {

    /**
     * Gets the [solar times][SolarTimes] for given coordinates and a given date
     *
     * @param coordinates Geographic coordinates
     * @param date Local date at the given coordinates
     *
     * @return The [solar times][SolarTimes] on the given date at the given location
     */
    operator fun invoke(coordinates: Coordinates, date: LocalDate): SolarTimes =
        SolarTimes(
            astronomicalDawn = solarTimesRepository.getAstronomicalDawn(coordinates, date),
            nauticalDawn = solarTimesRepository.getNauticalDawn(coordinates, date),
            civilDawn = solarTimesRepository.getCivilDawn(coordinates, date),
            sunrise = solarTimesRepository.getSunrise(coordinates, date),
            sunset = solarTimesRepository.getSunset(coordinates, date),
            civilDusk = solarTimesRepository.getCivilDusk(coordinates, date),
            nauticalDusk = solarTimesRepository.getNauticalDusk(coordinates, date),
            astronomicalDusk = solarTimesRepository.getAstronomicalDusk(coordinates, date),
        )
}
