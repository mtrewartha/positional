package io.trewartha.positional.ui.location

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.R
import io.trewartha.positional.ui.Screen
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val packageName = LocalContext.current.packageName
    val state by viewModel.state.collectAsState()
    LocationContent(
        state = state,
        events = viewModel.events,
        onCopyClick = viewModel::onCopyClick,
        onHelpClick = viewModel::onHelpClick,
        onNavigateToHelp = { navController.navigate(Screen.Help.route) },
        onNavigateToSettings = { context.navigateToSettings(packageName) },
        onScreenLockCheckedChange = viewModel::onScreenLockCheckedChange,
        onShareClick = viewModel::onShareClick
    )
}

@Composable
private fun LocationContent(
    state: LocationState?,
    events: Flow<LocationEvent>,
    onCopyClick: () -> Unit,
    onHelpClick: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
) {
    val activity = LocalContext.current.activity

    val flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

    val coordinatesCopyErrorMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_error)
    val coordinatesCopySuccessBothMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_both)
    val coordinatesCopySuccessLatitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_latitude)
    val coordinatesCopySuccessLongitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_longitude)
    val coordinatesShareErrorMessage =
        stringResource(R.string.location_snackbar_coordinates_share_error)
    val screenLockedMessage =
        stringResource(R.string.location_snackbar_screen_locked)
    val screenUnlockedMessage =
        stringResource(R.string.location_snackbar_screen_unlocked)

    val snackbarHostState = remember { SnackbarHostState() }

    val window = activity?.window

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val locationPermissions = remember {
                listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
            if (locationPermissionsState.allPermissionsGranted) {
                LocationLoadedContent(
                    state = state,
                    onShareClick = onShareClick,
                    onCopyClick = onCopyClick,
                    onScreenLockCheckedChange = onScreenLockCheckedChange,
                    onHelpClick = onHelpClick
                )
            } else if (locationPermissionsState.revokedPermissions.size == locationPermissions.size) {
                LocationPermissionRequiredContent(
                    locationPermissionsState = locationPermissionsState,
                    onNavigateToSettings = onNavigateToSettings
                )
            } else {
                TODO("Handle the case where COARSE location permission has been granted, but FINE has not")
            }
        }
    }
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                is LocationEvent.NavigateToLocationHelp ->
                    onNavigateToHelp()
                is LocationEvent.NavigateToSettings ->
                    onNavigateToSettings()
                is LocationEvent.ShowCoordinatesCopyErrorSnackbar -> launch {
                    snackbarHostState.showSnackbarWithDismissButton(coordinatesCopyErrorMessage)
                }
                is LocationEvent.ShowCoordinatesCopySuccessBothSnackbar -> launch {
                    snackbarHostState
                        .showSnackbarWithDismissButton(coordinatesCopySuccessBothMessage)
                }
                is LocationEvent.ShowCoordinatesCopySuccessLatitudeSnackbar -> launch {
                    snackbarHostState
                        .showSnackbarWithDismissButton(coordinatesCopySuccessLatitudeMessage)
                }
                is LocationEvent.ShowCoordinatesCopySuccessLongitudeSnackbar -> launch {
                    snackbarHostState
                        .showSnackbarWithDismissButton(coordinatesCopySuccessLongitudeMessage)
                }
                is LocationEvent.ShowCoordinatesShareErrorSnackbar -> launch {
                    snackbarHostState.showSnackbarWithDismissButton(coordinatesShareErrorMessage)
                }
                is LocationEvent.ShowCoordinatesShareSheet ->
                    activity?.let { a -> showCoordinatesShareSheet(a, it) }
                is LocationEvent.ShowScreenLockedSnackbar -> launch {
                    snackbarHostState.showSnackbarWithDismissButton(screenLockedMessage)
                }
                is LocationEvent.ShowScreenUnlockedSnackbar -> launch {
                    snackbarHostState.showSnackbarWithDismissButton(screenUnlockedMessage)
                }
            }
        }
    }
    DisposableEffect(state?.screenLockEnabled) {
        if (state?.screenLockEnabled == true) window?.addFlags(flagKeepScreenOn)
        else window?.clearFlags(flagKeepScreenOn)
        onDispose { window?.clearFlags(flagKeepScreenOn) }
    }
}

private fun showCoordinatesShareSheet(
    context: Context,
    event: LocationEvent.ShowCoordinatesShareSheet
) {
    startActivity(
        context,
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, event.coordinates)
            type = "text/plain"
        },
        null
    )
}

private fun Context.navigateToSettings(packageName: String) {
    val settingsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(settingsIntent)
}

private suspend fun SnackbarHostState.showSnackbarWithDismissButton(message: String) {
    currentSnackbarData?.dismiss()
    showSnackbar(message, withDismissAction = true)
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    LocationContent(
        state = null,
        events = emptyFlow(),
        onCopyClick = {},
        onHelpClick = {},
        onNavigateToHelp = {},
        onNavigateToSettings = {},
        onScreenLockCheckedChange = {},
        onShareClick = {}
    )
}