package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import kotlinx.coroutines.flow.Flow

class GetLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Location> = locationRepository.locationFlow
}
