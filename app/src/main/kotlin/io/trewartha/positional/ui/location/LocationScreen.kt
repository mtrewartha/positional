package io.trewartha.positional.ui.location

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val locationPermissions = remember {
            listOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
        if (locationPermissionsState.allPermissionsGranted) {
            AnimatedVisibility(
                visible = state == null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 24 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 24 })
            ) {
                LocationLoadingContent()
            }
            AnimatedVisibility(
                visible = state != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 24 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 24 })
            ) {
                if (state == null) return@AnimatedVisibility
                LocationLoadedContent(
                    state = state,
                    onShareClick = onShareClick,
                    onCopyClick = onCopyClick,
                    onScreenLockCheckedChange = onScreenLockCheckedChange,
                    onHelpClick = onHelpClick
                )
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
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

    LaunchedEffect(events) {
        events.collect {
            when (it) {
                is LocationEvent.NavigateToLocationHelp ->
                    onNavigateToHelp()
                is LocationEvent.NavigateToSettings ->
                    onNavigateToSettings()
                is LocationEvent.ShowCoordinatesCopyErrorSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopyErrorMessage)
                is LocationEvent.ShowCoordinatesCopySuccessBothSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessBothMessage)
                is LocationEvent.ShowCoordinatesCopySuccessLatitudeSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessLatitudeMessage)
                is LocationEvent.ShowCoordinatesCopySuccessLongitudeSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessLongitudeMessage)
                is LocationEvent.ShowCoordinatesShareErrorSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesShareErrorMessage)
                is LocationEvent.ShowCoordinatesShareSheet ->
                    activity?.let { a -> showCoordinatesShareSheet(a, it) }
                is LocationEvent.ShowScreenLockedSnackbar ->
                    snackbarHostState.showSnackbar(screenLockedMessage)
                is LocationEvent.ShowScreenUnlockedSnackbar ->
                    snackbarHostState.showSnackbar(screenUnlockedMessage)
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