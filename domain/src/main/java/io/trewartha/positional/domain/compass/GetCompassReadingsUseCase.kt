package io.trewartha.positional.domain.compass

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for a use case for getting [compass readings][CompassReading]
 */
interface GetCompassReadingsUseCase {

    /**
     * Gets a [Flow] that emits the current [compass reading[CompassReading] any time it changes
     *
     * @return [Flow] that emits the current [compass reading][CompassReading] any time it changes
     */
    operator fun invoke(): Flow<CompassReading>
}
