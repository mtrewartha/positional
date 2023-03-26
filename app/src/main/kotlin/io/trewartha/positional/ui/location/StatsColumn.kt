package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val showAccuracies = stats?.showAccuracies ?: false
        val placeholdersVisible = stats == null
        StatRow(
            icon = Icons.Rounded.Adjust,
            name = stringResource(R.string.location_label_accuracy),
            value = stats?.accuracy,
            accuracy = null,
            showAccuracy = showAccuracies,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = Icons.Rounded.Explore,
            name = stringResource(R.string.location_label_bearing),
            value = stats?.bearing,
            accuracy = stats?.bearingAccuracy,
            showAccuracy = showAccuracies,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = Icons.Rounded.Height,
            name = stringResource(R.string.location_label_altitude),
            value = stats?.altitude,
            accuracy = stats?.altitudeAccuracy,
            showAccuracy = showAccuracies,
            showPlaceholder = placeholdersVisible
        )
        StatRow(
            icon = Icons.Rounded.Speed,
            name = stringResource(R.string.location_label_speed),
            value = stats?.speed,
            accuracy = stats?.speedAccuracy,
            showAccuracy = showAccuracies,
            showPlaceholder = placeholdersVisible
        )
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
                    accuracy = "123",
                    bearing = "123",
                    bearingAccuracy = "± 3",
                    altitude = "123",
                    altitudeAccuracy = "± 300",
                    speed = "123",
                    speedAccuracy = "± 300",
                    showAccuracies = true,
                    updatedAt = "Updated at 12:00:00 PM"
                )
            )
        }
    }
}
