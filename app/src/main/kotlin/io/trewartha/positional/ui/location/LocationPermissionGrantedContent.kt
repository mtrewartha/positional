package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Launch
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Divider
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.AutoSizeText
import io.trewartha.positional.ui.utils.placeholder

@Composable
fun LocationPermissionGrantedContent(
    state: LocationState?,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onShareClick: () -> Unit,
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
            if (state == null) {
                Text(
                    text = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .placeholder(visible = true)
                )
            } else {
                AutoSizeText(
                    text = state.coordinates,
                    maxLines = state.maxLines,
                    textAlign = TextAlign.Center
                )
            }
        }
        ButtonRow(
            state = state,
            onCopyClick = onCopyClick,
            onLaunchClick = onLaunchClick,
            onShareClick = onShareClick
        )
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        StatsColumn(state?.stats)
    }
}

@Composable
private fun ButtonRow(
    state: LocationState?,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLaunchClick, enabled = state != null) {
            Icon(
                Icons.Rounded.Launch,
                stringResource(R.string.location_launch_button_content_description),
            )
        }
        IconButton(onClick = onShareClick, enabled = state != null) {
            Icon(
                Icons.Rounded.Share,
                stringResource(R.string.location_share_button_content_description),
            )
        }
        IconButton(onClick = onCopyClick, enabled = state != null) {
            Icon(
                Icons.Rounded.FileCopy,
                stringResource(R.string.location_coordinates_copy_content_description),
            )
        }
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            LocationPermissionGrantedContent(
                state = LocationState(
                    coordinates = "123.456789\n123.456789",
                    maxLines = 2,
                    screenLockEnabled = false,
                    coordinatesForCopy = "Coordinates for copy",
                    stats = LocationState.Stats(
                        accuracy = "123.4",
                        bearing = "123.4",
                        bearingAccuracy = "± 56.7",
                        altitude = "123.4",
                        altitudeAccuracy = "± 56.7",
                        speed = "123.4",
                        speedAccuracy = "± 56.7",
                        showAccuracies = true,
                        updatedAt = "12:00:00 PM"
                    ),
                ),
                onCopyClick = {},
                onLaunchClick = {},
                onShareClick = {}
            )
        }
    }
}
