package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Divider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews

@Composable
fun StatsColumn(
    stats: LocationState.Stats?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val accuracyVisible = stats?.showAccuracies ?: false
        val placeholdersVisible = stats == null
        Divider(modifier = Modifier.fillMaxWidth())
        StatRow(
            icon = Icons.Rounded.Adjust,
            name = stringResource(R.string.location_label_accuracy),
            value = stats?.accuracy,
            accuracy = null,
            accuracyVisible = accuracyVisible,
            placeholdersVisible = placeholdersVisible
        )
        Divider(modifier = Modifier.fillMaxWidth())
        StatRow(
            icon = Icons.Rounded.Explore,
            name = stringResource(R.string.location_label_bearing),
            value = stats?.bearing,
            accuracy = stats?.bearingAccuracy,
            accuracyVisible = accuracyVisible,
            placeholdersVisible = placeholdersVisible
        )
        Divider(modifier = Modifier.fillMaxWidth())
        StatRow(
            icon = Icons.Rounded.Height,
            name = stringResource(R.string.location_label_altitude),
            value = stats?.altitude,
            accuracy = stats?.altitudeAccuracy,
            accuracyVisible = accuracyVisible,
            placeholdersVisible = placeholdersVisible
        )
        Divider(modifier = Modifier.fillMaxWidth())
        StatRow(
            icon = Icons.Rounded.Speed,
            name = stringResource(R.string.location_label_speed),
            value = stats?.speed,
            accuracy = stats?.speedAccuracy,
            accuracyVisible = accuracyVisible,
            placeholdersVisible = placeholdersVisible
        )
        Divider(modifier = Modifier.fillMaxWidth())
        Text(
            text = stats?.updatedAt ?: "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 12.dp)
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
                LocationState.Stats(
                    accuracy = "123.4",
                    bearing = "123.4",
                    bearingAccuracy = null,
                    altitude = "123.4",
                    altitudeAccuracy = null,
                    speed = "123.4",
                    speedAccuracy = null,
                    showAccuracies = true,
                    updatedAt = "Updated at 12:00:00 PM"
                )
            )
        }
    }
}
