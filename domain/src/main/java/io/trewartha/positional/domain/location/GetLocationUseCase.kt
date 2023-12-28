package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.Locator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locator: Locator
) {
    operator fun invoke(): Flow<Location> = locator.location
}
