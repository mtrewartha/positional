package io.trewartha.positional.ui.compass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.North
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.location.StatRow

@Composable
fun StatsColumn(
    state: CompassViewModel.State.SensorsPresent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val placeholdersVisible = state is CompassViewModel.State.SensorsPresent.Loading
        StatRow(
            icon = Icons.Rounded.North,
            name = stringResource(R.string.compass_mode_label),
            value = when ((state as? CompassViewModel.State.SensorsPresent.Loaded)?.mode) {
                null -> ""
                CompassMode.MAGNETIC_NORTH ->
                    stringResource(R.string.compass_mode_value_magnetic_north)
                CompassMode.TRUE_NORTH ->
                    stringResource(R.string.compass_mode_value_true_north)
            },
            accuracy = null,
            showAccuracy = false,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = ImageVector.vectorResource(R.drawable.ic_angle_acute_24px),
            name = stringResource(R.string.compass_declination_label),
            value = (state as? CompassViewModel.State.SensorsPresent.Loaded)
                ?.magneticDeclination
                ?.let { stringResource(R.string.compass_declination, it.inDegrees().value) },
            accuracy = null,
            showAccuracy = false,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = Icons.Rounded.Adjust,
            name = stringResource(R.string.compass_label_accelerometer_accuracy),
            value = stringResource(
                when (
                    (state as? CompassViewModel.State.SensorsPresent.Loaded)?.accelerometerAccuracy
                ) {
                    CompassAccuracy.HIGH -> R.string.compass_accuracy_high
                    CompassAccuracy.MEDIUM -> R.string.compass_accuracy_medium
                    CompassAccuracy.LOW -> R.string.compass_accuracy_low
                    CompassAccuracy.UNRELIABLE -> R.string.compass_accuracy_unreliable
                    CompassAccuracy.UNUSABLE -> R.string.compass_accuracy_unusable
                    null -> R.string.compass_accuracy_unknown
                }
            ),
            accuracy = null,
            showAccuracy = false,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = Icons.Rounded.Adjust,
            name = stringResource(R.string.compass_label_magnetometer_accuracy),
            value = stringResource(
                when (
                    (state as? CompassViewModel.State.SensorsPresent.Loaded)?.magnetometerAccuracy
                ) {
                    CompassAccuracy.HIGH -> R.string.compass_accuracy_high
                    CompassAccuracy.MEDIUM -> R.string.compass_accuracy_medium
                    CompassAccuracy.LOW -> R.string.compass_accuracy_low
                    CompassAccuracy.UNRELIABLE -> R.string.compass_accuracy_unreliable
                    CompassAccuracy.UNUSABLE -> R.string.compass_accuracy_unusable
                    null -> R.string.compass_accuracy_unknown
                }
            ),
            accuracy = null,
            showAccuracy = false,
            showPlaceholder = placeholdersVisible
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun StatsColumnLoadedPreview() {
    PositionalTheme {
        Surface {
            StatsColumn(
                CompassViewModel.State.SensorsPresent.Loaded(
                    rotationMatrix = FloatArray(9),
                    accelerometerAccuracy = CompassAccuracy.HIGH,
                    magnetometerAccuracy = null,
                    magneticDeclination = Angle.Degrees(10f),
                    mode = CompassMode.TRUE_NORTH
                )
            )
        }
    }
}
