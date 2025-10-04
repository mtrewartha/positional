package io.trewartha.positional.compass

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * [Compass] implementation for use in testing classes that depend on a [Compass]
 */
public class TestCompass : Compass {

    override val azimuth: Flow<Azimuth>
        get() = _azimuth.distinctUntilChanged()

    private val _azimuth = MutableSharedFlow<Azimuth>(replay = 1)

    /**
     * Sets the compass azimuth
     *
     * @param azimuth Azimuth to set
     */
    public suspend fun setAzimuth(azimuth: Azimuth) {
        _azimuth.emit(azimuth)
    }
}
