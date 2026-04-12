package io.trewartha.positional.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.trewartha.positional.AppScope
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.settings.proto.CompassMode as WireCompassMode
import io.trewartha.positional.settings.proto.CompassNorthVibration as WireCompassNorthVibration
import io.trewartha.positional.settings.proto.CoordinatesFormat as WireCoordinatesFormat
import io.trewartha.positional.settings.proto.LocationAccuracyVisibility as WireLocationAccuracyVisibility
import io.trewartha.positional.settings.proto.Settings as WireSettings
import io.trewartha.positional.settings.proto.Theme as WireTheme
import io.trewartha.positional.settings.proto.Units as WireUnits
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [SettingsRepository] implementation powered by the AndroidX DataStore library
 */
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
public class DataStoreSettingsRepository(
    private val application: Application
) : SettingsRepository {

    private val Context.settingsDataStore: DataStore<WireSettings> by dataStore(
        fileName = "settings.proto",
        serializer = SettingsSerializer
    )

    override val compassMode: Flow<CompassMode> =
        application.settingsDataStore.data.map { it.compass_mode.toData() }

    override val compassNorthVibration: Flow<CompassNorthVibration> =
        application.settingsDataStore.data.map { it.compass_north_vibration.toData() }

    override val coordinatesFormat: Flow<CoordinatesFormat> =
        application.settingsDataStore.data.map { it.coordinates_format.toData() }

    override val locationAccuracyVisibility: Flow<LocationAccuracyVisibility> =
        application.settingsDataStore.data.map { it.location_accuracy_visibility.toData() }

    override val theme: Flow<Theme> =
        application.settingsDataStore.data.map { it.theme.toData() }

    override val units: Flow<Units> =
        application.settingsDataStore.data.map { it.units.toData() }

    override suspend fun setCompassMode(compassMode: CompassMode) {
        application.settingsDataStore.updateData {
            it.copy(compass_mode = compassMode.toWire())
        }
    }

    override suspend fun setCompassNorthVibration(compassNorthVibration: CompassNorthVibration) {
        application.settingsDataStore.updateData {
            it.copy(compass_north_vibration = compassNorthVibration.toWire())
        }
    }

    override suspend fun setCoordinatesFormat(coordinatesFormat: CoordinatesFormat) {
        application.settingsDataStore.updateData {
            it.copy(coordinates_format = coordinatesFormat.toWire())
        }
    }

    override suspend fun setLocationAccuracyVisibility(visibility: LocationAccuracyVisibility) {
        application.settingsDataStore.updateData {
            it.copy(location_accuracy_visibility = visibility.toWire())
        }
    }

    override suspend fun setTheme(theme: Theme) {
        application.settingsDataStore.updateData {
            it.copy(theme = theme.toWire())
        }
    }

    override suspend fun setUnits(units: Units) {
        application.settingsDataStore.updateData {
            it.copy(units = units.toWire())
        }
    }
}

private object SettingsSerializer : Serializer<WireSettings> {

    override val defaultValue: WireSettings = WireSettings()

    override suspend fun readFrom(input: InputStream): WireSettings =
        try {
            WireSettings.ADAPTER.decode(input)
        } catch (exception: IOException) {
            throw CorruptionException("Unable to read protobuf", exception)
        }

    override suspend fun writeTo(t: WireSettings, output: OutputStream) =
        WireSettings.ADAPTER.encode(output, t)
}

private fun CompassMode.toWire(): WireCompassMode =
    when (this) {
        CompassMode.MAGNETIC_NORTH -> WireCompassMode.COMPASS_MODE_MAGNETIC_NORTH
        CompassMode.TRUE_NORTH -> WireCompassMode.COMPASS_MODE_TRUE_NORTH
    }

private fun WireCompassMode.toData(): CompassMode =
    when (this) {
        WireCompassMode.COMPASS_MODE_TRUE_NORTH,
        WireCompassMode.COMPASS_MODE_UNSPECIFIED -> CompassMode.TRUE_NORTH
        WireCompassMode.COMPASS_MODE_MAGNETIC_NORTH -> CompassMode.MAGNETIC_NORTH
    }

private fun CompassNorthVibration.toWire(): WireCompassNorthVibration =
    when (this) {
        CompassNorthVibration.NONE -> WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_NONE
        CompassNorthVibration.SHORT -> WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_SHORT
        CompassNorthVibration.MEDIUM -> WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_MEDIUM
        CompassNorthVibration.LONG -> WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_LONG
    }

private fun WireCompassNorthVibration.toData(): CompassNorthVibration =
    when (this) {
        WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_SHORT -> CompassNorthVibration.SHORT
        WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_MEDIUM,
        WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_UNSPECIFIED -> CompassNorthVibration.MEDIUM
        WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_LONG -> CompassNorthVibration.LONG
        WireCompassNorthVibration.COMPASS_NORTH_VIBRATION_NONE -> CompassNorthVibration.NONE
    }

private fun CoordinatesFormat.toWire(): WireCoordinatesFormat =
    when (this) {
        CoordinatesFormat.DD -> WireCoordinatesFormat.COORDINATES_FORMAT_DECIMAL_DEGREES
        CoordinatesFormat.DDM -> WireCoordinatesFormat.COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES
        CoordinatesFormat.DMS -> WireCoordinatesFormat.COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS
        CoordinatesFormat.MGRS -> WireCoordinatesFormat.COORDINATES_FORMAT_MGRS
        CoordinatesFormat.UTM -> WireCoordinatesFormat.COORDINATES_FORMAT_UTM
    }

private fun WireCoordinatesFormat.toData(): CoordinatesFormat =
    when (this) {
        WireCoordinatesFormat.COORDINATES_FORMAT_DECIMAL_DEGREES,
        WireCoordinatesFormat.COORDINATES_FORMAT_UNSPECIFIED -> CoordinatesFormat.DD
        WireCoordinatesFormat.COORDINATES_FORMAT_DEGREES_DECIMAL_MINUTES -> CoordinatesFormat.DDM
        WireCoordinatesFormat.COORDINATES_FORMAT_DEGREES_MINUTES_SECONDS -> CoordinatesFormat.DMS
        WireCoordinatesFormat.COORDINATES_FORMAT_MGRS -> CoordinatesFormat.MGRS
        WireCoordinatesFormat.COORDINATES_FORMAT_UTM -> CoordinatesFormat.UTM
    }

private fun LocationAccuracyVisibility.toWire(): WireLocationAccuracyVisibility =
    when (this) {
        LocationAccuracyVisibility.HIDE ->
            WireLocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_HIDE
        LocationAccuracyVisibility.SHOW ->
            WireLocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_SHOW
    }

private fun WireLocationAccuracyVisibility.toData(): LocationAccuracyVisibility =
    when (this) {
        WireLocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_HIDE ->
            LocationAccuracyVisibility.HIDE
        WireLocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_SHOW,
        WireLocationAccuracyVisibility.LOCATION_ACCURACY_VISIBILITY_UNSPECIFIED ->
            LocationAccuracyVisibility.SHOW
    }

private fun Theme.toWire(): WireTheme =
    when (this) {
        Theme.DEVICE -> WireTheme.THEME_SYSTEM
        Theme.LIGHT -> WireTheme.THEME_LIGHT
        Theme.DARK -> WireTheme.THEME_DARK
    }

private fun WireTheme.toData(): Theme =
    when (this) {
        WireTheme.THEME_DARK -> Theme.DARK
        WireTheme.THEME_LIGHT -> Theme.LIGHT
        WireTheme.THEME_SYSTEM,
        WireTheme.THEME_UNSPECIFIED -> Theme.DEVICE
    }

private fun Units.toWire(): WireUnits =
    when (this) {
        Units.IMPERIAL -> WireUnits.UNITS_IMPERIAL
        Units.METRIC -> WireUnits.UNITS_METRIC
    }

private fun WireUnits.toData(): Units =
    when (this) {
        WireUnits.UNITS_IMPERIAL,
        WireUnits.UNITS_UNSPECIFIED -> Units.IMPERIAL
        WireUnits.UNITS_METRIC -> Units.METRIC
    }
