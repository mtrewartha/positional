package io.trewartha.positional.data.location

import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.core.measurement.Distance
import io.trewartha.positional.model.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock

/**
 * [Locator] implementation for use in testing classes that depend on a [Locator]
 */
class TestLocator : Locator {

    override val location: Flow<Location>
        get() = _location.distinctUntilChanged()

    private val _location = MutableSharedFlow<Location>(replay = 1)

    /**
     * Sets the current location
     *
     * @param location the location to set
     */
    suspend fun setLocation(location: Location = getTestLocation()) {
        _location.emit(location)
    }

    private fun getTestLocation(): Location =
        Location(
            timestamp = Clock.System.now(),
            coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
            altitude = Distance.Meters(0.0f),
            magneticDeclination = null
        )
}
