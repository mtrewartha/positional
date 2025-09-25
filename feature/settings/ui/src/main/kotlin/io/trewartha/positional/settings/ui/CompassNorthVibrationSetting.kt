package io.trewartha.positional.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.settings.CompassNorthVibration

@Composable
public fun CompassNorthVibrationSetting(
    value: CompassNorthVibration?,
    onValueChange: (CompassNorthVibration) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.Vibration,
        title = stringResource(R.string.feature_settings_ui_compass_north_vibration_title),
        values = CompassNorthVibration.entries.toSet(),
        value = value,
        valueName = { compassNorthVibration ->
            stringResource(
                when (compassNorthVibration) {
                    CompassNorthVibration.NONE ->
                        R.string.feature_settings_ui_compass_north_vibration_value_none
                    CompassNorthVibration.SHORT ->
                        R.string.feature_settings_ui_compass_north_vibration_value_short
                    CompassNorthVibration.MEDIUM ->
                        R.string.feature_settings_ui_compass_north_vibration_value_medium
                    CompassNorthVibration.LONG ->
                        R.string.feature_settings_ui_compass_north_vibration_value_long
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.feature_settings_ui_compass_north_vibration_dialog_title),
        valuesDialogText = null,
        onValueChange = onValueChange,
        modifier = modifier
    )
}
