package io.trewartha.positional.data.compass

import kotlinx.coroutines.flow.Flow

interface CompassReadingsRepository {

    val compassReadingsFlow: Flow<CompassReadings>
}
