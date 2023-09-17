package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.ui.PositionalTheme
import kotlinx.datetime.Clock

@Composable
fun StatsView(
    state: LocationState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val placeholdersVisible = state.timestamp == null
        AccuracyRow(
            accuracy = state.horizontalAccuracy,
            units = state.units,
            showAccuracy = state.showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        BearingRow(
            bearing = state.bearing,
            accuracy = state.bearingAccuracy,
            showAccuracy = state.showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        AltitudeRow(
            altitude = state.altitude,
            accuracy = state.altitudeAccuracy,
            units = state.units,
            showAccuracy = state.showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        SpeedRow(
            speed = state.speed,
            accuracy = state.speedAccuracy,
            units = state.units,
            showAccuracy = state.showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
    }
}

@Composable
private fun AccuracyRow(
    accuracy: Distance?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Adjust,
        name = stringResource(R.string.location_label_accuracy),
        value = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC ->
                    R.string.location_horizontal_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL ->
                    R.string.location_horizontal_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        accuracy = null,
        showAccuracy = showAccuracy ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun AltitudeRow(
    altitude: Distance?,
    accuracy: Distance?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Height,
        name = stringResource(R.string.location_label_altitude),
        value = if (altitude == null || units == null) {
            null
        } else {
            val (stringResource, convertedAltitude) = when (units) {
                Units.METRIC -> R.string.location_altitude_metric to altitude.inMeters()
                Units.IMPERIAL -> R.string.location_altitude_imperial to altitude.inFeet()
            }
            stringResource(stringResource, convertedAltitude.value)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.location_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL -> R.string.location_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        showAccuracy = showAccuracy ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun BearingRow(
    bearing: Angle?,
    accuracy: Angle?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Explore,
        name = stringResource(R.string.location_label_bearing),
        value = if (bearing == null) {
            null
        } else {
            stringResource(R.string.location_bearing, bearing.inDegrees().value)
        },
        accuracy = if (accuracy == null) {
            null
        } else {
            stringResource(R.string.location_bearing_accuracy, accuracy.inDegrees().value)
        },
        showAccuracy = showAccuracy ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun SpeedRow(
    speed: Speed?,
    accuracy: Speed?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Speed,
        name = stringResource(R.string.location_label_speed),
        value = if (speed == null || units == null) {
            null
        } else {
            val (stringResource, convertedSpeed) = when (units) {
                Units.METRIC -> R.string.location_speed_metric to speed.inKilometersPerHour()
                Units.IMPERIAL -> R.string.location_speed_imperial to speed.inMilesPerHour()
            }
            stringResource(stringResource, convertedSpeed.value)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.location_accuracy_metric to accuracy.inKilometersPerHour()
                Units.IMPERIAL -> R.string.location_accuracy_imperial to accuracy.inMilesPerHour()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        showAccuracy = showAccuracy ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@PreviewFontScale
@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            StatsView(
                state = LocationState(
                    coordinates = Coordinates(
                        latitude = 123.456789,
                        longitude = 234.567890
                    ),
                    horizontalAccuracy = Distance.Meters(3456.789f),
                    bearing = Angle.Degrees(123.456f),
                    bearingAccuracy = Angle.Degrees(1.23f),
                    altitude = Distance.Meters(12345.678f),
                    altitudeAccuracy = Distance.Meters(123.456f),
                    speed = Speed.KilometersPerHour(123.456f),
                    speedAccuracy = Speed.KilometersPerHour(12.345f),
                    timestamp = Clock.System.now(),
                    coordinatesFormat = CoordinatesFormat.DD,
                    units = Units.METRIC,
                    showAccuracies = true,
                    screenLockedOn = false
                ),
            )
        }
    }
}
