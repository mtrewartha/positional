package io.trewartha.positional.domain.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.settings.CompassModeProto
import io.trewartha.positional.data.settings.CoordinatesFormatProto
import io.trewartha.positional.data.settings.SettingsProto.Settings
import io.trewartha.positional.data.settings.ThemeProto
import io.trewartha.positional.data.settings.UnitsProto
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.data.units.Units
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(private val context: Context) : SettingsRepository {

    private val Context.settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "settings.proto",
        serializer = SettingsSerializer
    )

    override val compassMode: Flow<CompassMode> =
        context.settingsDataStore.data.map { it.compassMode.toData() }

    override val coordinatesFormat: Flow<CoordinatesFormat> =
        context.settingsDataStore.data.map { it.coordinatesFormat.toData() }

    override val screenLockEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { it.screenLockEnabled }

    override val hideAccuracies: Flow<Boolean> =
        context.settingsDataStore.data.map { it.hideAccuracies }

    override val theme: Flow<Theme> =
        context.settingsDataStore.data.map { it.theme.toData() }

    override val units: Flow<Units> =
        context.settingsDataStore.data.map { it.units.toData() }

    override suspend fun setCompassMode(compassMode: CompassMode) {
        context.settingsDataStore.updateData {
            it.toBuilder().setCompassMode(compassMode.toProto()).build()
        }
    }

    override suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat) {
        context.settingsDataStore.updateData {
            it.toBuilder().setCoordinatesFormat(coordinatesFormat.toProto()).build()
        }
    }

    override suspend fun setScreenLockEnabled(screenLockEnabled: Boolean) {
        context.settingsDataStore.updateData {
            it.toBuilder().setScreenLockEnabled(screenLockEnabled).build()
        }
    }

    override suspend fun setHideAccuracies(hideAccuracies: Boolean) {
        context.settingsDataStore.updateData {
            it.toBuilder().setHideAccuracies(hideAccuracies).build()
        }
    }

    override suspend fun setTheme(theme: Theme) {
        context.settingsDataStore.updateData {
            it.toBuilder().setTheme(theme.toProto()).build()
        }
    }

    override suspend fun setUnits(units: Units) {
        context.settingsDataStore.updateData {
            it.toBuilder().setUnits(units.toProto()).build()
        }
    }

    private fun CompassMode.toProto(): CompassModeProto.CompassMode =
        when (this) {
            CompassMode.MAGNETIC_NORTH -> CompassModeProto.CompassMode.MAGNETIC_NORTH
            CompassMode.TRUE_NORTH -> CompassModeProto.CompassMode.TRUE_NORTH
        }

    private fun CompassModeProto.CompassMode.toData(): CompassMode =
        when (this) {
            CompassModeProto.CompassMode.TRUE_NORTH,
            CompassModeProto.CompassMode.UNRECOGNIZED -> CompassMode.TRUE_NORTH
            CompassModeProto.CompassMode.MAGNETIC_NORTH -> CompassMode.MAGNETIC_NORTH
        }

    private fun CoordinatesFormat.toProto(): CoordinatesFormatProto.CoordinatesFormat =
        when (this) {
            CoordinatesFormat.DD -> CoordinatesFormatProto.CoordinatesFormat.DD
            CoordinatesFormat.DDM -> CoordinatesFormatProto.CoordinatesFormat.DDM
            CoordinatesFormat.DMS -> CoordinatesFormatProto.CoordinatesFormat.DMS
            CoordinatesFormat.MGRS -> CoordinatesFormatProto.CoordinatesFormat.MGRS
            CoordinatesFormat.UTM -> CoordinatesFormatProto.CoordinatesFormat.UTM
        }

    private fun CoordinatesFormatProto.CoordinatesFormat.toData(): CoordinatesFormat =
        when (this) {
            CoordinatesFormatProto.CoordinatesFormat.DD,
            CoordinatesFormatProto.CoordinatesFormat.UNRECOGNIZED -> CoordinatesFormat.DD
            CoordinatesFormatProto.CoordinatesFormat.DDM -> CoordinatesFormat.DDM
            CoordinatesFormatProto.CoordinatesFormat.DMS -> CoordinatesFormat.DMS
            CoordinatesFormatProto.CoordinatesFormat.MGRS -> CoordinatesFormat.MGRS
            CoordinatesFormatProto.CoordinatesFormat.UTM -> CoordinatesFormat.UTM
        }

    private fun Theme.toProto(): ThemeProto.Theme =
        when (this) {
            Theme.DEVICE -> ThemeProto.Theme.SYSTEM
            Theme.LIGHT -> ThemeProto.Theme.LIGHT
            Theme.DARK -> ThemeProto.Theme.DARK
        }

    private fun ThemeProto.Theme.toData(): Theme =
        when (this) {
            ThemeProto.Theme.SYSTEM,
            ThemeProto.Theme.UNRECOGNIZED -> Theme.DEVICE
            ThemeProto.Theme.LIGHT -> Theme.LIGHT
            ThemeProto.Theme.DARK -> Theme.DARK
        }

    private fun Units.toProto(): UnitsProto.Units =
        when (this) {
            Units.IMPERIAL -> UnitsProto.Units.IMPERIAL
            Units.METRIC -> UnitsProto.Units.METRIC
        }

    private fun UnitsProto.Units.toData(): Units =
        when (this) {
            UnitsProto.Units.IMPERIAL,
            UnitsProto.Units.UNRECOGNIZED -> Units.IMPERIAL
            UnitsProto.Units.METRIC -> Units.METRIC
        }
}
