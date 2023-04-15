package io.trewartha.positional.domain.settings

import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.data.units.Units
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val compassMode: Flow<CompassMode>

    val coordinatesFormat: Flow<CoordinatesFormat>

    val screenLockEnabled: Flow<Boolean>

    val hideAccuracies: Flow<Boolean>

    val theme: Flow<Theme>

    val units: Flow<Units>

    suspend fun setCompassMode(compassMode: CompassMode)

    suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat)

    suspend fun setScreenLockEnabled(screenLockEnabled: Boolean)

    suspend fun setHideAccuracies(hideAccuracies: Boolean)

    suspend fun setTheme(theme: Theme)

    suspend fun setUnits(units: Units)
}