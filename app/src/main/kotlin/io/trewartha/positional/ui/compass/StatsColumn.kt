package io.trewartha.positional.ui.compass

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.Compass
import io.trewartha.positional.ui.Divider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.location.StatRow

@Composable
fun StatsColumn(state: CompassViewModel.State.SensorsPresent) {
    Column {
        val placeholdersVisible = state is CompassViewModel.State.SensorsPresent.Loading
        val (accelerometerAccuracy, magnetometerAccuracy) =
            (state as? CompassViewModel.State.SensorsPresent.Loaded)
                ?.let { it.accelerometerAccuracy to it.magnetometerAccuracy }
                ?: (null to null)
        AnimatedVisibility(
            visible = !placeholdersVisible &&
                    (accelerometerAccuracy == null ||
                            magnetometerAccuracy == null ||
                            accelerometerAccuracy <= Compass.Accuracy.LOW ||
                            magnetometerAccuracy <= Compass.Accuracy.LOW),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.compass_accuracy_warning_title),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.compass_accuracy_warning_body),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        val dividerIndent = 16.dp
        StatRow(
            icon = Icons.Rounded.North,
            name = stringResource(R.string.compass_mode_label),
            value = when ((state as? CompassViewModel.State.SensorsPresent.Loaded)?.mode) {
                null -> ""
                CompassMode.MAGNETIC_NORTH ->
                    stringResource(R.string.compass_mode_value_magnetic_north)
                CompassMode.TRUE_NORTH ->
                    stringResource(R.string.compass_mode_value_magnetic_north)
            },
            accuracy = null,
            accuracyVisible = false,
            placeholdersVisible = placeholdersVisible
        )
        Divider(modifier = Modifier.padding(horizontal = dividerIndent))
        StatRow(
            icon = ImageVector.vectorResource(R.drawable.ic_angle_acute_24px),
            name = stringResource(R.string.compass_declination_label),
            value = (state as? CompassViewModel.State.SensorsPresent.Loaded)
                ?.magneticDeclinationDegrees
                ?.let { stringResource(R.string.compass_declination, it) },
            accuracy = null,
            accuracyVisible = false,
            placeholdersVisible = placeholdersVisible
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
                    accelerometerAccuracy = Compass.Accuracy.HIGH,
                    magnetometerAccuracy = null,
                    magneticDeclinationDegrees = 10f,
                    mode = CompassMode.TRUE_NORTH
                )
            )
        }
    }
}