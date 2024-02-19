package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.model.core.measurement.Angle
import io.trewartha.positional.model.core.measurement.Distance
import io.trewartha.positional.model.core.measurement.Speed
import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.settings.LocationAccuracyVisibility

@Composable
fun AccuracyBlock(
    accuracy: Distance?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Adjust,
        name = stringResource(R.string.ui_location_label_accuracy),
        value = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC ->
                    R.string.ui_location_horizontal_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL ->
                    R.string.ui_location_horizontal_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        accuracy = null,
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
fun AltitudeBlock(
    altitude: Distance?,
    accuracy: Distance?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Height,
        name = stringResource(R.string.ui_location_label_altitude),
        value = if (altitude == null || units == null) {
            null
        } else {
            val (stringResource, convertedAltitude) = when (units) {
                Units.METRIC -> R.string.ui_location_altitude_metric to altitude.inMeters()
                Units.IMPERIAL -> R.string.ui_location_altitude_imperial to altitude.inFeet()
            }
            stringResource(stringResource, convertedAltitude.value)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.ui_location_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL -> R.string.ui_location_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
fun BearingBlock(
    bearing: Angle?,
    accuracy: Angle?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.NorthEast,
        name = stringResource(R.string.ui_location_label_bearing),
        value = if (bearing == null) {
            null
        } else {
            stringResource(R.string.ui_location_bearing, bearing.inDegrees().value)
        },
        accuracy = if (accuracy == null) {
            null
        } else {
            stringResource(R.string.ui_location_bearing_accuracy, accuracy.inDegrees().value)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
fun SpeedBlock(
    speed: Speed?,
    accuracy: Speed?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Speed,
        name = stringResource(R.string.ui_location_label_speed),
        value = if (speed == null || units == null) {
            null
        } else {
            val (stringResource, convertedSpeed) = when (units) {
                Units.METRIC -> R.string.ui_location_speed_metric to speed.inKilometersPerHour()
                Units.IMPERIAL -> R.string.ui_location_speed_imperial to speed.inMilesPerHour()
            }
            stringResource(stringResource, convertedSpeed.value)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.ui_location_accuracy_metric to accuracy.inKilometersPerHour()
                Units.IMPERIAL -> R.string.ui_location_accuracy_imperial to accuracy.inMilesPerHour()
            }
            stringResource(stringResource, convertedAccuracy.value)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}
