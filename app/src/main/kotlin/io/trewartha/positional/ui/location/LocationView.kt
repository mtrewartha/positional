package io.trewartha.positional.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.IconToggleButton
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

fun NavGraphBuilder.locationView(
    onNavigateToInfo: () -> Unit,
    onNavigateToMap: (Double, Double, LocalDateTime) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable(NavDestination.Location.route) {
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
        val viewModel: LocationViewModel = hiltViewModel()
        val locationState by viewModel.locationState.collectAsState()
        val screenLockEnabled by viewModel.screenLockEnabled.collectAsState()

        LocationView(
            locationState = locationState,
            screenLockEnabled = screenLockEnabled,
            events = viewModel.events,
            locationPermissionsState = locationPermissionsState,
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
    locationState: LocationState?,
    screenLockEnabled: Boolean?,
    locationPermissionsState: MultiplePermissionsState,
    events: Flow<LocationEvent>,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToMap: (Double, Double, LocalDateTime) -> Unit,
    onNavigateToSettings: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            LocationTopAppBar(
                screenLockEnabled = screenLockEnabled,
                onInfoClick = onNavigateToInfo,
                onScreenLockCheckedChange = onScreenLockCheckedChange,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (locationPermissionsState.allPermissionsGranted) {
                LocationPermissionGrantedContent(
                    locationState = locationState,
                    onCopyClick = onCopyClick,
                    onLaunchClick = onLaunchClick,
                    onShareClick = onShareClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.standard_padding))
                )
            } else if (
                with(locationPermissionsState) { revokedPermissions.size == permissions.size }
            ) {
                LocationPermissionRequiredContent(
                    locationPermissionsState = locationPermissionsState,
                    onNavigateToSettings = onNavigateToSettings,
                    modifier = Modifier
                        .widthIn(max = 384.dp)
                        .padding(dimensionResource(R.dimen.standard_padding))
                )
            } else {
                TODO("Handle the case where COARSE location permission has been granted, but FINE has not")
            }
        }
    }

    val activity = LocalContext.current.activity
    val window = activity?.window
    val coordinatesCopySuccessBothMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_both)
    val coordinatesCopySuccessLatitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_latitude)
    val coordinatesCopySuccessLongitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_longitude)
    val screenLockedMessage =
        stringResource(R.string.location_snackbar_screen_locked)
    val screenUnlockedMessage =
        stringResource(R.string.location_snackbar_screen_unlocked)
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                is LocationEvent.NavigateToGeoActivity ->
                    onNavigateToMap(it.latitude, it.longitude, it.localDateTime)
                is LocationEvent.NavigateToSettings ->
                    onNavigateToSettings()
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

    DisposableEffect(screenLockEnabled) {
        if (screenLockEnabled == true) window?.addFlags(FLAG_KEEP_SCREEN_ON)
        else window?.clearFlags(FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(FLAG_KEEP_SCREEN_ON) }
    }
}

@Composable
private fun LocationTopAppBar(
    screenLockEnabled: Boolean?,
    onInfoClick: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        actions = {
            IconToggleButton(
                checked = screenLockEnabled ?: false,
                onCheckedChange = onScreenLockCheckedChange,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ScreenLockPortrait,
                    contentDescription = stringResource(
                        if (screenLockEnabled == true)
                            R.string.location_screen_lock_button_content_description_on
                        else
                            R.string.location_screen_lock_button_content_description_off
                    ),
                )
            }
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = stringResource(R.string.location_button_info_content_description),
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
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
private fun PermissionNotGrantedPreview() {
    PositionalTheme {
        LocationView(
            locationState = null,
            screenLockEnabled = null,
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = false
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
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
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        LocationView(
            locationState = null,
            screenLockEnabled = null,
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = true
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
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
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        LocationView(
            locationState = LocationState(
                location = Location(
                    latitude = 123.456789,
                    longitude = 123.456789,
                    horizontalAccuracyMeters = 123.45678f,
                    bearingDegrees = 123.45678f,
                    bearingAccuracyDegrees = 123.45678f,
                    altitudeMeters = 123.45678,
                    altitudeAccuracyMeters = 123.45678f,
                    speedMetersPerSecond = 123.45678f,
                    speedAccuracyMetersPerSecond = 123.45678f,
                    timestamp = Instant.DISTANT_PAST,
                    magneticDeclinationDegrees = 123.45678f
                ),
                coordinatesFormat = CoordinatesFormat.DD,
                units = Units.METRIC,
                showAccuracies = true
            ),
            screenLockEnabled = true,
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = true
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
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
}
