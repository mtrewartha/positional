package io.trewartha.positional.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import io.trewartha.positional.core.measurement.Units
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * [SettingsRepository] implementation powered by the AndroidX DataStore library
 */
internal class DataStoreSettingsRepository @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val Context.settingsDataStore: DataStore<SettingsProto.Settings> by dataStore(
        fileName = "settings.proto",
        serializer = SettingsSerializer
    )

    override val compassMode: Flow<CompassMode> =
        context.settingsDataStore.data.map { it.compassMode.toData() }

    override val compassNorthVibration: Flow<CompassNorthVibration> =
        context.settingsDataStore.data.map { it.compassNorthVibration.toData() }

    override val coordinatesFormat: Flow<CoordinatesFormat> =
        context.settingsDataStore.data.map { it.coordinatesFormat.toData() }

    override val locationAccuracyVisibility: Flow<LocationAccuracyVisibility> =
        context.settingsDataStore.data.map { it.locationAccuracyVisibility.toData() }

    override val theme: Flow<Theme> =
        context.settingsDataStore.data.map { it.theme.toData() }

    override val units: Flow<Units> =
        context.settingsDataStore.data.map { it.units.toData() }

    override suspend fun setCompassMode(compassMode: CompassMode) {
        context.settingsDataStore.updateData {
            it.toBuilder().setCompassMode(compassMode.toProto()).build()
        }
    }

    override suspend fun setCompassNorthVibration(compassNorthVibration: CompassNorthVibration) {
        context.settingsDataStore.updateData {
            it.toBuilder().setCompassNorthVibration(compassNorthVibration.toProto()).build()
        }
    }

    override suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat) {
        context.settingsDataStore.updateData {
            it.toBuilder().setCoordinatesFormat(coordinatesFormat.toProto()).build()
        }
    }

    override suspend fun setLocationAccuracyVisibility(visibility: LocationAccuracyVisibility) {
        context.settingsDataStore.updateData {
            it.toBuilder().setLocationAccuracyVisibility(visibility.toProto()).build()
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
}

private object SettingsSerializer : Serializer<SettingsProto.Settings> {

    override val defaultValue: SettingsProto.Settings = SettingsProto.Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingsProto.Settings =
        try {
            SettingsProto.Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Unable to read protobuf", exception)
        }

    override suspend fun writeTo(t: SettingsProto.Settings, output: OutputStream) =
        t.writeTo(output)
}

private fun CompassMode.toProto(): CompassModeProto.CompassMode =
    when (this) {
        CompassMode.MAGNETIC_NORTH -> CompassModeProto.CompassMode.COMPASS_MODE_MAGNETIC_NORTH
        CompassMode.TRUE_NORTH -> CompassModeProto.CompassMode.COMPASS_MODE_TRUE_NORTH
    }

private fun CompassModeProto.CompassMode.toData(): CompassMode =
    when (this) {
        CompassModeProto.CompassMode.COMPASS_MODE_TRUE_NORTH,
        CompassModeProto.CompassMode.COMPASS_MODE_UNSPECIFIED,
        CompassModeProto.CompassMode.UNRECOGNIZED -> CompassMode.TRUE_NORTH
        CompassModeProto.CompassMode.COMPASS_MODE_MAGNETIC_NORTH -> CompassMode.MAGNETIC_NORTH
    }

private fun CompassNorthVibration.toProto(): CompassNorthVibrationProto.CompassNorthVibration =
    when (this) {
        CompassNorthVibration.NONE ->
            CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_NONE
        CompassNorthVibration.SHORT ->
            CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_SHORT
        CompassNorthVibration.MEDIUM ->
            CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_MEDIUM
        CompassNorthVibration.LONG ->
            CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_LONG
    }

private fun CompassNorthVibrationProto.CompassNorthVibration.toData(): CompassNorthVibration =
    when (this) {
        CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_SHORT ->
            CompassNorthVibration.SHORT
        CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_MEDIUM,
        CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_UNSPECIFIED,
        CompassNorthVibrationProto.CompassNorthVibration.UNRECOGNIZED ->
            CompassNorthVibration.MEDIUM
        CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_LONG ->
            CompassNorthVibration.LONG
        CompassNorthVibrationProto.CompassNorthVibration.COMPASS_NORTH_VIBRATION_NONE ->
            CompassNorthVibration.NONE
    }

private fun CoordinatesFormat.toProto(): CoordinatesFormatProto.CoordinatesFormat =
    when (this) {
        CoordinatesFormat.DD ->
            CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DECIMAL_DEGREES
        CoordinatesFormat.DDM ->
            CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES
        CoordinatesFormat.DMS ->
            CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS
        CoordinatesFormat.MGRS ->
            CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_MGRS
        CoordinatesFormat.UTM ->
            CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_UTM
    }

private fun CoordinatesFormatProto.CoordinatesFormat.toData(): CoordinatesFormat =
    when (this) {
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DECIMAL_DEGREES,
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_UNSPECIFIED,
        CoordinatesFormatProto.CoordinatesFormat.UNRECOGNIZED ->
            CoordinatesFormat.DD
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES ->
            CoordinatesFormat.DDM
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS ->
            CoordinatesFormat.DMS
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_MGRS ->
            CoordinatesFormat.MGRS
        CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_UTM ->
            CoordinatesFormat.UTM
    }

private fun LocationAccuracyVisibility.toProto(): LocationAccuracyVisibilityProto.LocationAccuracyVisibility =
    when (this) {
        LocationAccuracyVisibility.HIDE ->
            LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_HIDE
        LocationAccuracyVisibility.SHOW ->
            LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_SHOW
    }

private fun LocationAccuracyVisibilityProto.LocationAccuracyVisibility.toData(): LocationAccuracyVisibility =
    when (this) {
        LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_HIDE ->
            LocationAccuracyVisibility.HIDE
        LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_SHOW,
        LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_UNSPECIFIED,
        LocationAccuracyVisibilityProto.LocationAccuracyVisibility.UNRECOGNIZED ->
            LocationAccuracyVisibility.SHOW
    }

private fun Theme.toProto(): ThemeProto.Theme =
    when (this) {
        Theme.DEVICE -> ThemeProto.Theme.THEME_SYSTEM
        Theme.LIGHT -> ThemeProto.Theme.THEME_LIGHT
        Theme.DARK -> ThemeProto.Theme.THEME_DARK
    }

private fun ThemeProto.Theme.toData(): Theme =
    when (this) {
        ThemeProto.Theme.THEME_DARK -> Theme.DARK
        ThemeProto.Theme.THEME_LIGHT -> Theme.LIGHT
        ThemeProto.Theme.THEME_SYSTEM,
        ThemeProto.Theme.THEME_UNSPECIFIED,
        ThemeProto.Theme.UNRECOGNIZED -> Theme.DEVICE
    }

private fun Units.toProto(): UnitsProto.Units =
    when (this) {
        Units.IMPERIAL -> UnitsProto.Units.UNITS_IMPERIAL
        Units.METRIC -> UnitsProto.Units.UNITS_METRIC
    }

private fun UnitsProto.Units.toData(): Units =
    when (this) {
        UnitsProto.Units.UNITS_IMPERIAL,
        UnitsProto.Units.UNITS_UNSPECIFIED,
        UnitsProto.Units.UNRECOGNIZED -> Units.IMPERIAL
        UnitsProto.Units.UNITS_METRIC -> Units.METRIC
    }
