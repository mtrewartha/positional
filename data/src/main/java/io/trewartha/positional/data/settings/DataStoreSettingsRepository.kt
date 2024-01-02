package io.trewartha.positional.data.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.settings.CompassModeProto.CompassMode.COMPASS_MODE_MAGNETIC_NORTH
import io.trewartha.positional.data.settings.CompassModeProto.CompassMode.COMPASS_MODE_TRUE_NORTH
import io.trewartha.positional.data.settings.CompassModeProto.CompassMode.COMPASS_MODE_UNSPECIFIED
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DECIMAL_DEGREES
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_MGRS
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_UNSPECIFIED
import io.trewartha.positional.data.settings.CoordinatesFormatProto.CoordinatesFormat.COORDINATES_FORMAT_UTM
import io.trewartha.positional.data.settings.LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_HIDE
import io.trewartha.positional.data.settings.LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_SHOW
import io.trewartha.positional.data.settings.LocationAccuracyVisibilityProto.LocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_UNSPECIFIED
import io.trewartha.positional.data.settings.LocationAccuracyVisibilityProto.LocationAccuracyVisibility.UNRECOGNIZED
import io.trewartha.positional.data.settings.SettingsProto.Settings
import io.trewartha.positional.data.settings.ThemeProto.Theme.THEME_DARK
import io.trewartha.positional.data.settings.ThemeProto.Theme.THEME_LIGHT
import io.trewartha.positional.data.settings.ThemeProto.Theme.THEME_SYSTEM
import io.trewartha.positional.data.settings.ThemeProto.Theme.THEME_UNSPECIFIED
import io.trewartha.positional.data.settings.UnitsProto.Units.UNITS_IMPERIAL
import io.trewartha.positional.data.settings.UnitsProto.Units.UNITS_METRIC
import io.trewartha.positional.data.settings.UnitsProto.Units.UNITS_UNSPECIFIED
import io.trewartha.positional.data.ui.LocationAccuracyVisibility
import io.trewartha.positional.data.ui.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * [SettingsRepository] implementation powered by the AndroidX DataStore library
 */
class DataStoreSettingsRepository @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val Context.settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "settings.proto",
        serializer = SettingsSerializer
    )

    override val compassMode: Flow<CompassMode> =
        context.settingsDataStore.data.map { it.compassMode.toData() }

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

    private fun CompassMode.toProto(): CompassModeProto.CompassMode =
        when (this) {
            CompassMode.MAGNETIC_NORTH -> COMPASS_MODE_MAGNETIC_NORTH
            CompassMode.TRUE_NORTH -> COMPASS_MODE_TRUE_NORTH
        }

    private fun CompassModeProto.CompassMode.toData(): CompassMode =
        when (this) {
            COMPASS_MODE_TRUE_NORTH,
            COMPASS_MODE_UNSPECIFIED,
            CompassModeProto.CompassMode.UNRECOGNIZED -> CompassMode.TRUE_NORTH
            COMPASS_MODE_MAGNETIC_NORTH -> CompassMode.MAGNETIC_NORTH
        }

    private fun CoordinatesFormat.toProto(): CoordinatesFormatProto.CoordinatesFormat =
        when (this) {
            CoordinatesFormat.DD -> COORDINATES_FORMAT_DECIMAL_DEGREES
            CoordinatesFormat.DDM -> COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES
            CoordinatesFormat.DMS -> COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS
            CoordinatesFormat.MGRS -> COORDINATES_FORMAT_MGRS
            CoordinatesFormat.UTM -> COORDINATES_FORMAT_UTM
        }

    private fun CoordinatesFormatProto.CoordinatesFormat.toData(): CoordinatesFormat =
        when (this) {
            COORDINATES_FORMAT_DECIMAL_DEGREES,
            COORDINATES_FORMAT_UNSPECIFIED,
            CoordinatesFormatProto.CoordinatesFormat.UNRECOGNIZED -> CoordinatesFormat.DD
            COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES -> CoordinatesFormat.DDM
            COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS -> CoordinatesFormat.DMS
            COORDINATES_FORMAT_MGRS -> CoordinatesFormat.MGRS
            COORDINATES_FORMAT_UTM -> CoordinatesFormat.UTM
        }

    private fun LocationAccuracyVisibility.toProto(): LocationAccuracyVisibilityProto.LocationAccuracyVisibility =
        when (this) {
            LocationAccuracyVisibility.HIDE -> LOCATION_ACCURACY_VISIBILITY_HIDE
            LocationAccuracyVisibility.SHOW -> LOCATION_ACCURACY_VISIBILITY_SHOW
        }

    private fun LocationAccuracyVisibilityProto.LocationAccuracyVisibility.toData(): LocationAccuracyVisibility =
        when (this) {
            LOCATION_ACCURACY_VISIBILITY_HIDE -> LocationAccuracyVisibility.HIDE
            LOCATION_ACCURACY_VISIBILITY_SHOW,
            LOCATION_ACCURACY_VISIBILITY_UNSPECIFIED,
            UNRECOGNIZED -> LocationAccuracyVisibility.SHOW
        }

    private fun Theme.toProto(): ThemeProto.Theme =
        when (this) {
            Theme.DEVICE -> THEME_SYSTEM
            Theme.LIGHT -> THEME_LIGHT
            Theme.DARK -> THEME_DARK
        }

    private fun ThemeProto.Theme.toData(): Theme =
        when (this) {
            THEME_DARK -> Theme.DARK
            THEME_LIGHT -> Theme.LIGHT
            THEME_SYSTEM,
            THEME_UNSPECIFIED,
            ThemeProto.Theme.UNRECOGNIZED -> Theme.DEVICE
        }

    private fun Units.toProto(): UnitsProto.Units =
        when (this) {
            Units.IMPERIAL -> UNITS_IMPERIAL
            Units.METRIC -> UNITS_METRIC
        }

    private fun UnitsProto.Units.toData(): Units =
        when (this) {
            UNITS_IMPERIAL,
            UNITS_UNSPECIFIED,
            UnitsProto.Units.UNRECOGNIZED -> Units.IMPERIAL
            UNITS_METRIC -> Units.METRIC
        }
}

private object SettingsSerializer : Serializer<Settings> {

    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings =
        try {
            Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Unable to read protobuf", exception)
        }

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}
