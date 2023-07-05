package io.trewartha.positional.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.IconToggleButton
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.activity
import io.trewartha.positional.ui.utils.format.coordinates.CoordinatesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesDecimalMinutesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesMinutesSecondsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.MgrsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.UtmFormatter
import kotlinx.datetime.Instant

fun NavGraphBuilder.locationView(
    onAndroidSettingsClick: () -> Unit,
    onInfoClick: () -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit
) {
    composable(NavDestination.Location.route) {
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)

        val viewModel: LocationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides when (state.coordinatesFormat) {
                CoordinatesFormat.DD -> DecimalDegreesFormatter(context, locale)
                CoordinatesFormat.DDM -> DegreesDecimalMinutesFormatter(context, locale)
                CoordinatesFormat.DMS -> DegreesMinutesSecondsFormatter(context, locale)
                CoordinatesFormat.MGRS -> MgrsFormatter(context)
                CoordinatesFormat.UTM -> UtmFormatter(context, locale)
            }
        ) {
            val coordinatesFormatter = LocalCoordinatesFormatter.current
            LocationView(
                locationPermissionsState = locationPermissionsState,
                state = state,
                onAndroidSettingsClick = onAndroidSettingsClick,
                onScreenLockToggle = { locked ->
                    viewModel.events.trySend(LocationEvent.LockToggle(locked))
                },
                onInfoClick = onInfoClick,
                onCopyClick = { coordinates ->
                    copyCoordinates(coordinates, coordinatesFormatter, clipboardManager)
                },
                onMapClick = onMapClick,
                onShareClick = { coordinates ->
                    shareCoordinates(context, coordinatesFormatter, coordinates)
                }
            )
            DisposableEffect(state.screenLockedOn) {
                val window = context.activity?.window?.apply {
                    if (state.screenLockedOn) {
                        addFlags(FLAG_KEEP_SCREEN_ON)
                    } else {
                        clearFlags(FLAG_KEEP_SCREEN_ON)
                    }
                }
                onDispose {
                    window?.clearFlags(FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }
}

@Composable
private fun LocationView(
    locationPermissionsState: MultiplePermissionsState,
    state: LocationState,
    onAndroidSettingsClick: () -> Unit,
    onScreenLockToggle: (Boolean) -> Unit,
    onInfoClick: () -> Unit,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            LocationTopAppBar(
                screenLockedOn = state.screenLockedOn,
                onInfoClick = onInfoClick,
                onScreenLockToggle = onScreenLockToggle,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        if (locationPermissionsState.allPermissionsGranted) {
            LocationPermissionGrantedContent(
                state = state,
                onCopyClick = onCopyClick,
                onMapClick = onMapClick,
                onShareClick = onShareClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
                    .padding(dimensionResource(R.dimen.standard_padding))
            )
        } else if (
            with(locationPermissionsState) { revokedPermissions.size == permissions.size }
        ) {
            LocationPermissionRequiredContent(
                locationPermissionsState = locationPermissionsState,
                onSettingsClick = onAndroidSettingsClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
                    .padding(dimensionResource(R.dimen.standard_padding))
            )
        } else {
            TODO("Handle the case where COARSE location permission has been granted, but FINE has not")
        }
    }

    val activity = LocalContext.current.activity
    val window = activity?.window

//    val coordinatesCopySuccessBothMessage =
//        stringResource(R.string.location_snackbar_coordinates_copy_success_both)
//    val coordinatesCopySuccessLatitudeMessage =
//        stringResource(R.string.location_snackbar_coordinates_copy_success_latitude)
//    val coordinatesCopySuccessLongitudeMessage =
//        stringResource(R.string.location_snackbar_coordinates_copy_success_longitude)
//    val screenLockedMessage =
//        stringResource(R.string.location_snackbar_screen_locked)
//    val screenUnlockedMessage =
//        stringResource(R.string.location_snackbar_screen_unlocked)
//    LaunchedEffect(events) {
//        events.collect {
//            when (it) {
//                is LocationEvent.NavigateToGeoActivity ->
//                    onNavigateToMap(it.latitude, it.longitude, it.localDateTime)
//                is LocationEvent.NavigateToSettings ->
//                    onNavigateToSettings()
//                is LocationEvent.ShowCoordinatesCopySuccessBothSnackbar -> launch {
//                    snackbarHostState
//                        .showSnackbarWithDismissButton(coordinatesCopySuccessBothMessage)
//                }
//                is LocationEvent.ShowCoordinatesCopySuccessLatitudeSnackbar -> launch {
//                    snackbarHostState
//                        .showSnackbarWithDismissButton(coordinatesCopySuccessLatitudeMessage)
//                }
//                is LocationEvent.ShowCoordinatesCopySuccessLongitudeSnackbar -> launch {
//                    snackbarHostState
//                        .showSnackbarWithDismissButton(coordinatesCopySuccessLongitudeMessage)
//                }
//                is LocationEvent.ShowCoordinatesShareSheet ->
//                    activity?.let { a -> showCoordinatesShareSheet(a, it) }
//                is LocationEvent.ShowScreenLockedSnackbar -> launch {
//                    snackbarHostState.showSnackbarWithDismissButton(screenLockedMessage)
//                }
//                is LocationEvent.ShowScreenUnlockedSnackbar -> launch {
//                    snackbarHostState.showSnackbarWithDismissButton(screenUnlockedMessage)
//                }
//            }
//        }
//    }
}

@Composable
private fun LocationTopAppBar(
    screenLockedOn: Boolean?,
    onInfoClick: () -> Unit,
    onScreenLockToggle: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val window = LocalContext.current.activity?.window ?: return
    DisposableEffect(screenLockedOn) {
        if (screenLockedOn == true) {
            window.addFlags(FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(FLAG_KEEP_SCREEN_ON)
        }
        onDispose { window.clearFlags(FLAG_KEEP_SCREEN_ON) }
    }
    TopAppBar(
        title = {},
        modifier = modifier,
        actions = {
            IconToggleButton(
                checked = screenLockedOn ?: false,
                onCheckedChange = onScreenLockToggle,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ScreenLockPortrait,
                    contentDescription = stringResource(
                        if (screenLockedOn == true)
                            R.string.location_button_lock_content_description_on
                        else
                            R.string.location_button_lock_content_description_off
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

private fun copyCoordinates(
    coordinates: Coordinates?,
    coordinatesFormatter: CoordinatesFormatter,
    clipboardManager: ClipboardManager
) {
    if (coordinates == null) return
    val formattedCoordinates = coordinatesFormatter.formatForCopy(coordinates)
    clipboardManager.setText(AnnotatedString(formattedCoordinates))
}

private fun shareCoordinates(
    context: Context,
    coordinatesFormatter: CoordinatesFormatter,
    coordinates: Coordinates?
) {
    if (coordinates == null) return
    val formattedCoordinates = coordinatesFormatter.formatForCopy(coordinates)
    startActivity(
        context,
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, formattedCoordinates)
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
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = false
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
            state = LocationState(
                coordinates = null,
                coordinatesFormat = CoordinatesFormat.DD,
                horizontalAccuracyMeters = null,
                bearingDegrees = null,
                bearingAccuracyDegrees = null,
                altitudeMeters = null,
                altitudeAccuracyMeters = null,
                speedMetersPerSecond = null,
                speedAccuracyMetersPerSecond = null,
                timestamp = null,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {},
            onInfoClick = {},
            onCopyClick = {},
            onMapClick = { _, _ -> },
            onShareClick = {}
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LocatingPreview() {
    PositionalTheme {
        LocationView(
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = true
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
            state = LocationState(
                coordinates = null,
                coordinatesFormat = CoordinatesFormat.DD,
                horizontalAccuracyMeters = null,
                bearingDegrees = null,
                bearingAccuracyDegrees = null,
                altitudeMeters = null,
                altitudeAccuracyMeters = null,
                speedMetersPerSecond = null,
                speedAccuracyMetersPerSecond = null,
                timestamp = null,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {},
            onInfoClick = {},
            onCopyClick = {},
            onMapClick = { _, _ -> },
            onShareClick = {}
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LocatedPreview() {
    PositionalTheme {
        LocationView(
            locationPermissionsState = object : MultiplePermissionsState {
                override val allPermissionsGranted: Boolean = true
                override val permissions: List<PermissionState> = emptyList()
                override val revokedPermissions: List<PermissionState> = emptyList()
                override val shouldShowRationale: Boolean = false
                override fun launchMultiplePermissionRequest() {
                    // Don't do anything
                }
            },
            state = LocationState(
                coordinates = Coordinates(
                    latitude = 123.456789,
                    longitude = 123.456789
                ),
                coordinatesFormat = CoordinatesFormat.DD,
                horizontalAccuracyMeters = 123.45678f,
                bearingDegrees = 123.45678f,
                bearingAccuracyDegrees = 123.45678f,
                altitudeMeters = 123.45678f,
                altitudeAccuracyMeters = 123.45678f,
                speedMetersPerSecond = 123.45678f,
                speedAccuracyMetersPerSecond = 123.45678f,
                timestamp = Instant.DISTANT_PAST,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {},
            onInfoClick = {},
            onCopyClick = {},
            onMapClick = { _, _ -> },
            onShareClick = {}
        )
    }
}
