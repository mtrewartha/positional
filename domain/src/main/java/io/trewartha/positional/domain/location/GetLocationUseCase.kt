package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for a use case for getting [location][Location]
 */
interface GetLocationUseCase {

    /**
     * Gets a [Flow] that emits the current device [location][Location] any time it changes
     *
     * @return [Flow] that emits the current device [location][Location] any time it changes
     */
    operator fun invoke(): Flow<Location>
}
