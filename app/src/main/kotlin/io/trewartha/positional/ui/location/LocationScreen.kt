package io.trewartha.positional.ui.location

import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R

@Composable
fun LocationScreen(
    coordinatesState: CoordinatesState,
    screenLockEnabled: Boolean,
    locationStatsState: LocationStatsState,
    onCopyClick: () -> Unit,
    onHelpClick: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    LocationScreenLockSnackbar(screenLockEnabled, scaffoldState)
    Scaffold(scaffoldState = scaffoldState) {
        Surface {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.background(MaterialTheme.colors.surface)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .fillMaxSize()
                ) {
                    CoordinatesText(coordinatesState, modifier = Modifier.align(Alignment.Center))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareButton(onShareClick)
                    CopyButton(onCopyClick)
                    ScreenLockButton(screenLockEnabled, onScreenLockCheckedChange)
                    HelpButton(onHelpClick)
                }
                LocationStatsColumn(locationStatsState)
            }
        }
    }
}

@Composable
private fun LocationScreenLockSnackbar(
    screenLockEnabled: Boolean,
    scaffoldState: ScaffoldState
) {
    val activity = LocalContext.current.activity
    val window = activity?.window
    val flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    val screenLockMessage = stringResource(
        if (screenLockEnabled) R.string.location_snackbar_screen_locked
        else R.string.location_snackbar_screen_unlocked
    )
    LaunchedEffect(screenLockEnabled) {
        scaffoldState.snackbarHostState.showSnackbar(screenLockMessage)
    }
    DisposableEffect(screenLockEnabled) {
        if (screenLockEnabled) {
            window?.addFlags(flagKeepScreenOn)
        } else {
            window?.clearFlags(flagKeepScreenOn)
        }
        onDispose {
            window?.clearFlags(flagKeepScreenOn)
        }
    }
}

private val Context.activity: AppCompatActivity?
    get() {
        return when (this) {
            is AppCompatActivity -> this
            is ContextWrapper -> baseContext.activity
            else -> null
        }
    }

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LocationScreen(
        coordinatesState = CoordinatesState("123.456789\n123.456789", 2),
        screenLockEnabled = true,
        locationStatsState = LocationStatsState(
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
        onCopyClick = {},
        onHelpClick = {},
        onScreenLockCheckedChange = {},
        onShareClick = {},
    )
}
