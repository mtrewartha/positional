package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Adjust
import androidx.compose.material.icons.twotone.Explore
import androidx.compose.material.icons.twotone.Speed
import androidx.compose.material.icons.twotone.Terrain
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews

@Composable
fun StatsColumn(
    accuracy: String,
    bearing: String,
    bearingAccuracy: String?,
    elevation: String,
    elevationAccuracy: String?,
    speed: String,
    speedAccuracy: String?,
    showAccuracies: Boolean,
    updatedAt: String,
) {
    Column {
        val dividerIndent = 16.dp
        LocationStatRow(
            icon = Icons.TwoTone.Adjust,
            name = stringResource(R.string.location_label_accuracy),
            value = accuracy,
            accuracy = null,
            accuracyVisible = showAccuracies
        )
        Divider(modifier = Modifier.padding(horizontal = dividerIndent))
        LocationStatRow(
            icon = Icons.TwoTone.Explore,
            name = stringResource(R.string.location_label_bearing),
            value = bearing,
            accuracy = bearingAccuracy,
            accuracyVisible = showAccuracies
        )
        Divider(modifier = Modifier.padding(horizontal = dividerIndent))
        LocationStatRow(
            icon = Icons.TwoTone.Terrain,
            name = stringResource(R.string.location_label_elevation),
            value = elevation,
            accuracy = elevationAccuracy,
            accuracyVisible = showAccuracies
        )
        Divider(modifier = Modifier.padding(horizontal = dividerIndent))
        LocationStatRow(
            icon = Icons.TwoTone.Speed,
            name = stringResource(R.string.location_label_speed),
            value = speed,
            accuracy = speedAccuracy,
            accuracyVisible = showAccuracies
        )
        Text(
            text = updatedAt,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            StatsColumn(
                accuracy = "123.4",
                bearing = "123.4",
                bearingAccuracy = null,
                elevation = "123.4",
                elevationAccuracy = null,
                speed = "123.4",
                speedAccuracy = null,
                showAccuracies = true,
                updatedAt = "Updated at 12:00:00 PM"
            )
        }
    }
}