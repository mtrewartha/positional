package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units

@Composable
fun AccuracyBlock(
    accuracy: Distance?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
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
fun AltitudeBlock(
    altitude: Distance?,
    accuracy: Distance?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
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
fun BearingBlock(
    bearing: Angle?,
    accuracy: Angle?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.NorthEast,
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
fun SpeedBlock(
    speed: Speed?,
    accuracy: Speed?,
    units: Units?,
    showAccuracy: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
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
