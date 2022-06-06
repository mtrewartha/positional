package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            ShareButton(onShareClick)
            CopyButton(onCopyClick)
            ScreenLockButton(state.screenLockEnabled, onScreenLockCheckedChange)
            HelpButton(onHelpClick)
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