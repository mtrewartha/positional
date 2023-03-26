package io.trewartha.positional.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.R
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

fun NavGraphBuilder.locationView(
    onNavigateToInfo: () -> Unit,
    onNavigateToMap: (Double, Double, LocalDateTime) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable(Location.route) {
        val viewModel: LocationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LocationView(
            state = state,
            events = viewModel.events,
            onCopyClick = viewModel::onCopyClick,
            onLaunchClick = viewModel::onLaunchClick,
            onNavigateToInfo = onNavigateToInfo,
            onNavigateToMap = onNavigateToMap,
            onNavigateToSettings = onNavigateToSettings,
            onScreenLockCheckedChange = viewModel::onScreenLockCheckedChange,
            onShareClick = viewModel::onShareClick
        )
    }
}

@Composable
private fun LocationView(
    state: LocationState?,
    events: Flow<LocationEvent>,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToMap: (Double, Double, LocalDateTime) -> Unit,
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

    Scaffold(
        topBar = { LocationTopAppBar(state, onNavigateToInfo, onScreenLockCheckedChange) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
            val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
            if (locationPermissionsState.allPermissionsGranted) {
                LocationPermissionGrantedContent(
                    state = state,
                    onCopyClick = onCopyClick,
                    onLaunchClick = onLaunchClick,
                    onShareClick = onShareClick,
                    modifier = Modifier.padding(16.dp)
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
                is LocationEvent.NavigateToGeoActivity ->
                    onNavigateToMap(it.latitude, it.longitude, it.localDateTime)
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

private suspend fun SnackbarHostState.showSnackbarWithDismissButton(message: String) {
    currentSnackbarData?.dismiss()
    showSnackbar(message, withDismissAction = true)
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    LocationView(
        state = null,
        events = emptyFlow(),
        onCopyClick = {},
        onLaunchClick = {},
        onNavigateToInfo = {},
        onNavigateToMap = { _, _, _ -> },
        onNavigateToSettings = {},
        onScreenLockCheckedChange = {},
        onShareClick = {}
    )
}
