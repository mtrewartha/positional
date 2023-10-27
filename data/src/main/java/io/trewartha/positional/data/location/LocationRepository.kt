package io.trewartha.positional.data.location

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
