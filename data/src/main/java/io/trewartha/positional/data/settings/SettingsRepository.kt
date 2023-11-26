package io.trewartha.positional.data.settings

import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.ui.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val compassMode: Flow<CompassMode>

    val coordinatesFormat: Flow<CoordinatesFormat>

    val showAccuracies: Flow<Boolean>

    val theme: Flow<Theme>

    val units: Flow<Units>

    suspend fun setCompassMode(compassMode: CompassMode)

    suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat)

    suspend fun setShowAccuracies(showAccuracies: Boolean)

    suspend fun setTheme(theme: Theme)

    suspend fun setUnits(units: Units)
}
