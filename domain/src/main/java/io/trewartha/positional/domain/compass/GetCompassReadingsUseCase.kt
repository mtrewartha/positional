package io.trewartha.positional.domain.compass

import kotlinx.coroutines.flow.Flow

interface GetCompassReadingsUseCase {

    operator fun invoke(): Flow<CompassReadings>
}
