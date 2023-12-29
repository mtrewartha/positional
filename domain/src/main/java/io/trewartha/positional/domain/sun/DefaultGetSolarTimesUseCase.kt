package io.trewartha.positional.domain.sun

import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.sun.SolarTimes
import io.trewartha.positional.data.sun.SolarTimesRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * [GetSolarTimesUseCase] implementation powered by a [SolarTimesRepository]
 */
class DefaultGetSolarTimesUseCase @Inject constructor(
    private val solarTimesRepository: SolarTimesRepository
) : GetSolarTimesUseCase {

    override operator fun invoke(coordinates: Coordinates, date: LocalDate): SolarTimes =
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
