package io.trewartha.positional.location.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.Distance
import io.trewartha.positional.core.measurement.Speed
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.settings.LocationAccuracyVisibility

@Composable
public fun AccuracyBlock(
    accuracy: Distance?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Adjust,
        name = stringResource(R.string.feature_location_ui_label_accuracy),
        value = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC ->
                    R.string.feature_location_ui_horizontal_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL ->
                    R.string.feature_location_ui_horizontal_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.magnitude)
        },
        accuracy = null,
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
public fun AltitudeBlock(
    altitude: Distance?,
    accuracy: Distance?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Height,
        name = stringResource(R.string.feature_location_ui_label_altitude),
        value = if (altitude == null || units == null) {
            null
        } else {
            val (stringResource, convertedAltitude) = when (units) {
                Units.METRIC -> R.string.feature_location_ui_altitude_metric to altitude.inMeters()
                Units.IMPERIAL -> R.string.feature_location_ui_altitude_imperial to altitude.inFeet()
            }
            stringResource(stringResource, convertedAltitude.magnitude)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.feature_location_ui_accuracy_metric to accuracy.inMeters()
                Units.IMPERIAL -> R.string.feature_location_ui_accuracy_imperial to accuracy.inFeet()
            }
            stringResource(stringResource, convertedAccuracy.magnitude)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
public fun BearingBlock(
    bearing: Angle?,
    accuracy: Angle?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.NorthEast,
        name = stringResource(R.string.feature_location_ui_label_bearing),
        value = if (bearing == null) {
            null
        } else {
            stringResource(R.string.feature_location_ui_bearing, bearing.inDegrees().magnitude)
        },
        accuracy = if (accuracy == null) {
            null
        } else {
            stringResource(R.string.feature_location_ui_bearing_accuracy, accuracy.inDegrees().magnitude)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
public fun SpeedBlock(
    speed: Speed?,
    accuracy: Speed?,
    units: Units?,
    accuracyVisibility: LocationAccuracyVisibility?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatBlock(
        icon = Icons.Rounded.Speed,
        name = stringResource(R.string.feature_location_ui_label_speed),
        value = if (speed == null || units == null) {
            null
        } else {
            val (stringResource, convertedSpeed) = when (units) {
                Units.METRIC -> R.string.feature_location_ui_speed_metric to speed.inKilometersPerHour()
                Units.IMPERIAL -> R.string.feature_location_ui_speed_imperial to speed.inMilesPerHour()
            }
            stringResource(stringResource, convertedSpeed.magnitude)
        },
        accuracy = if (accuracy == null || units == null) {
            null
        } else {
            val (stringResource, convertedAccuracy) = when (units) {
                Units.METRIC -> R.string.feature_location_ui_accuracy_metric to accuracy.inKilometersPerHour()
                Units.IMPERIAL -> R.string.feature_location_ui_accuracy_imperial to accuracy.inMilesPerHour()
            }
            stringResource(stringResource, convertedAccuracy.magnitude)
        },
        accuracyVisibility = accuracyVisibility,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}
