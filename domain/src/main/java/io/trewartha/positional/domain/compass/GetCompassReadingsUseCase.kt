package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassReadings
import io.trewartha.positional.data.compass.CompassReadingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompassReadingsUseCase @Inject constructor(
    private val compassReadingRepository: CompassReadingsRepository
) {
    operator fun invoke(): Flow<CompassReadings> = compassReadingRepository.compassReadingsFlow
}
