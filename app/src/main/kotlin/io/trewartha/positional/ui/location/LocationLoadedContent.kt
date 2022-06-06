package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.Help
import androidx.compose.material.icons.twotone.ScreenLockPortrait
import androidx.compose.material.icons.twotone.Share
import androidx.compose.material.icons.twotone.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.AutoSizeText

@Composable
fun LocationLoadedContent(
    state: LocationState,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AutoSizeText(
                text = state.coordinates,
                maxLines = state.maxLines
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.TwoTone.Share,
                    contentDescription = stringResource(R.string.location_share_button_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onCopyClick) {
                Icon(
                    imageVector = Icons.TwoTone.FileCopy,
                    contentDescription = stringResource(R.string.location_coordinates_copy_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconToggleButton(
                checked = state.screenLockEnabled,
                onCheckedChange = onScreenLockCheckedChange
            ) {
                Icon(
                    imageVector = if (state.screenLockEnabled)
                        Icons.TwoTone.Smartphone
                    else
                        Icons.TwoTone.ScreenLockPortrait,
                    contentDescription = stringResource(
                        if (state.screenLockEnabled)
                            R.string.location_screen_lock_button_content_description_on
                        else
                            R.string.location_screen_lock_button_content_description_off
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.TwoTone.Help,
                    contentDescription = stringResource(R.string.location_help_button_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        StatsColumn(
            accuracy = state.accuracy,
            bearing = state.bearing,
            bearingAccuracy = state.bearingAccuracy,
            elevation = state.elevation,
            elevationAccuracy = state.elevationAccuracy,
            speed = state.speed,
            speedAccuracy = state.speedAccuracy,
            showAccuracies = state.showAccuracies,
            updatedAt = state.updatedAt
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            LocationLoadedContent(
                state = LocationState(
                    coordinates = "123.456789\n123.456789",
                    maxLines = 2,
                    screenLockEnabled = false,
                    coordinatesForCopy = "Coordinates for copy",
                    accuracy = "123.4",
                    bearing = "123.4",
                    bearingAccuracy = "± 56.7",
                    elevation = "123.4",
                    elevationAccuracy = "± 56.7",
                    speed = "123.4",
                    speedAccuracy = "± 56.7",
                    showAccuracies = true,
                    updatedAt = "12:00:00 PM"
                ),
                onShareClick = {},
                onCopyClick = {},
                onScreenLockCheckedChange = {},
                onHelpClick = {}
            )
        }
    }
}