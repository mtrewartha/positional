package io.trewartha.positional.data.compass

import kotlinx.coroutines.flow.Flow

interface CompassAzimuthRepository {

    val compassAzimuth: Flow<CompassAzimuth>
}
