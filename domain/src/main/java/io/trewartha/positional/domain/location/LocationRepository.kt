package io.trewartha.positional.domain.location

import io.trewartha.positional.data.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Repository of [location][Location] data gathered from any sources in use by the implementation of
 * this interface.
 */
interface LocationRepository {

    /**
     * [Flow][Flow] of the latest [location][Location]. The source of the location is up to the
     * implementation of this interface.
     */
    val locationFlow: Flow<Location>
}
