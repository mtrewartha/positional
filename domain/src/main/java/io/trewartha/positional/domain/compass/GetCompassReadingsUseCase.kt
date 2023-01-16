package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassReadings
import kotlinx.coroutines.flow.Flow

class GetCompassReadingsUseCase(
    private val compassReadingRepository: CompassReadingsRepository
) {
    operator fun invoke(): Flow<CompassReadings> = compassReadingRepository.compassReadingsFlow
}
