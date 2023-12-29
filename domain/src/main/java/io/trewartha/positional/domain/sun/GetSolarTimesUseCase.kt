package io.trewartha.positional.domain.sun

import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.sun.SolarTimes
import kotlinx.datetime.LocalDate

/**
 * Abstraction for a use case for getting [solar times][SolarTimes]
 */
interface GetSolarTimesUseCase {

    /**
     * Gets the [solar times][SolarTimes] for given coordinates and a given date
     *
     * @param coordinates Geographic coordinates
     * @param date Local date at the given coordinates
     *
     * @return The [solar times][SolarTimes] on the given date at the given location
     */
    operator fun invoke(coordinates: Coordinates, date: LocalDate): SolarTimes
}
