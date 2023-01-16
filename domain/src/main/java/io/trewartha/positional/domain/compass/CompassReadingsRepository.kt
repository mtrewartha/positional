package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassReadings
import kotlinx.coroutines.flow.Flow

interface CompassReadingsRepository {

    val compassReadingsFlow: Flow<CompassReadings>
}
