package io.trewartha.positional.data.compass

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

/**
 * [Compass] implementation for use in testing classes that depend on a [Compass]
 */
class TestCompass : Compass {

    override val azimuth: Flow<CompassAzimuth>
        get() = _azimuth.filterNotNull()

    private val _azimuth = MutableStateFlow<CompassAzimuth?>(null)

    /**
     * Sets the current azimuth
     *
     * @param azimuth the azimuth to set
     */
    fun setAzimuth(azimuth: CompassAzimuth) {
        _azimuth.update { azimuth }
    }
}
