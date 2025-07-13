package io.trewartha.positional.data.location

import io.trewartha.positional.model.core.measurement.GeodeticCoordinates
import io.trewartha.positional.model.core.measurement.degrees
import io.trewartha.positional.model.core.measurement.meters
import io.trewartha.positional.model.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Clock

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
            coordinates = GeodeticCoordinates(latitude = 0.degrees, longitude = 0.degrees),
            altitude = 0.meters,
            magneticDeclination = null
        )
}
