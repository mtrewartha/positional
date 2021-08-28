package io.trewartha.positional.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Adjust
import androidx.compose.material.icons.twotone.Explore
import androidx.compose.material.icons.twotone.Speed
import androidx.compose.material.icons.twotone.Terrain
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Divider

data class LocationStatsState(
    val accuracy: String,
    val bearing: String,
    val bearingAccuracy: String?,
    val elevation: String,
    val elevationAccuracy: String?,
    val speed: String,
    val speedAccuracy: String?,
    val showAccuracies: Boolean,
    val updatedAt: String
)

@Composable
fun LocationStatsColumn(locationStatsState: LocationStatsState) {
    val locationStatRowStates = listOf(
        LocationStatRowState(
            icon = Icons.TwoTone.Adjust,
            name = stringResource(R.string.location_label_accuracy),
            value = locationStatsState.accuracy,
            accuracy = null,
            accuracyVisible = locationStatsState.showAccuracies
        ),
        LocationStatRowState(
            icon = Icons.TwoTone.Explore,
            name = stringResource(R.string.location_label_bearing),
            value = locationStatsState.bearing,
            accuracy = locationStatsState.bearingAccuracy,
            accuracyVisible = locationStatsState.showAccuracies
        ),
        LocationStatRowState(
            icon = Icons.TwoTone.Terrain,
            name = stringResource(R.string.location_label_elevation),
            value = locationStatsState.elevation,
            accuracy = locationStatsState.elevationAccuracy,
            accuracyVisible = locationStatsState.showAccuracies
        ),
        LocationStatRowState(
            icon = Icons.TwoTone.Speed,
            name = stringResource(R.string.location_label_speed),
            value = locationStatsState.speed,
            accuracy = locationStatsState.speedAccuracy,
            accuracyVisible = locationStatsState.showAccuracies
        )
    )
    Column(
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) {
        for (locationStatRowState in locationStatRowStates) {
            Divider()
            LocationStatRow(locationStatRowState = locationStatRowState)
        }
        Text(
            text = locationStatsState.updatedAt,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview("Without Accuracies")
@Composable
fun WithoutAccuraciesPreview() {
    LocationStatsColumn(
        locationStatsState = LocationStatsState(
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
    )
}

@Preview("Showing Accuracies")
@Composable
fun ShowingAccuraciesPreview() {
    LocationStatsColumn(
        locationStatsState = LocationStatsState(
            accuracy = "123.4",
            bearing = "123.4",
            bearingAccuracy = "± 56.7",
            elevation = "123.4",
            elevationAccuracy = "± 56.7",
            speed = "123.4",
            speedAccuracy = "± 56.7",
            showAccuracies = true,
            updatedAt = "Updated at 12:00:00 PM"
        )
    )
}

@Preview("Hiding Accuracies")
@Composable
fun HidingAccuraciesPreview() {
    LocationStatsColumn(
        locationStatsState = LocationStatsState(
            accuracy = "123.4",
            bearing = "123.4",
            bearingAccuracy = "± 56.7",
            elevation = "123.4",
            elevationAccuracy = "± 56.7",
            speed = "123.4",
            speedAccuracy = "± 56.7",
            showAccuracies = false,
            updatedAt = "Updated at 12:00:00 PM"
        )
    )
}