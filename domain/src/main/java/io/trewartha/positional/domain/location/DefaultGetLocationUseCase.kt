package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.location.Locator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [GetLocationUseCase] implementation powered by a [Locator]
 */
class DefaultGetLocationUseCase @Inject constructor(
    private val locator: Locator
) : GetLocationUseCase {

    override operator fun invoke(): Flow<Location> = locator.location
}
