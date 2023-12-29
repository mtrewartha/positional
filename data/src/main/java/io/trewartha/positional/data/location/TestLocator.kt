package io.trewartha.positional.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

/**
 * [Locator] implementation for use in testing classes that depend on a [Locator]
 */
class TestLocator : Locator {

    override val location: Flow<Location>
        get() = _location.filterNotNull()

    private val _location = MutableStateFlow<Location?>(null)

    /**
     * Sets the current location
     *
     * @param location the location to set
     */
    fun setLocation(location: Location) {
        _location.update { location }
    }
}
