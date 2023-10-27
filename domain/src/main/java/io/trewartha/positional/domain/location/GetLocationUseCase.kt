package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Location> = locationRepository.locationFlow
}
