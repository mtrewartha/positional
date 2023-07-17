package io.trewartha.positional.ui.location

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.IconToggleButton
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.activity
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.coordinates.CoordinatesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesDecimalMinutesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesMinutesSecondsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.MgrsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.UtmFormatter
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

fun NavGraphBuilder.locationView(
    onAndroidSettingsClick: () -> Unit
) {
    composable(NavDestination.Location.route) {
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)

        val viewModel: LocationViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
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
                }
            ) { coordinates ->
                shareCoordinates(context, coordinatesFormatter, coordinates)
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
    onShareClick: (Coordinates?) -> Unit,
) {
    // Snackbars
    val coroutineScope = rememberCoroutineScope()
    val coordinatesCopiedMessage = stringResource(R.string.location_snackbar_coordinates_copied)
    val screenLockMessage = stringResource(
        // This might look backwards, but it's not. We're setting the message to show *when the
        // state changes from the current value*, not the message to show *for* the current value.
        if (state.screenLockedOn) {
            R.string.location_snackbar_screen_unlocked
        } else {
            R.string.location_snackbar_screen_locked
        }
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showInfoSheet by rememberSaveable { mutableStateOf(false) }
    var showMapError by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            LocationTopAppBar(
                screenLockedOn = state.screenLockedOn,
                onInfoClick = { showInfoSheet = true },
                onScreenLockToggle = {
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(screenLockMessage)
                    }
                    onScreenLockToggle(it)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        if (locationPermissionsState.allPermissionsGranted) {
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val coordinatesFormatter = LocalCoordinatesFormatter.current
            val dateTimeFormatter = LocalDateTimeFormatter.current
            LocationPermissionGrantedContent(
                state = state,
                onCopyClick = { coordinates ->
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(coordinatesCopiedMessage)
                    }
                    copyCoordinates(coordinates, coordinatesFormatter, clipboardManager)
                },
                onMapClick = { coordinates, timestamp ->
                    try {
                        navigateToMap(context, dateTimeFormatter, coordinates, timestamp)
                    } catch (exception: ActivityNotFoundException) {
                        Timber.w(exception, "Unable to open map")
                        showMapError = true
                    }
                },
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
    if (showInfoSheet) LocationInfoSheet(onDismissRequest = { showInfoSheet = false })
    if (showMapError) MapErrorDialog(onDismissRequest = { showMapError = false })
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
                        if (screenLockedOn == true) {
                            R.string.location_button_lock_content_description_on
                        } else {
                            R.string.location_button_lock_content_description_off
                        }
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

@Composable
private fun MapErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.location_dialog_map_error_button_confirm))
            }
        },
        icon = { Icon(Icons.Rounded.ErrorOutline, contentDescription = null) },
        title = { Text(stringResource(R.string.location_dialog_map_error_title)) },
        text = { Text(stringResource(R.string.location_dialog_map_error_text)) }
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

@Throws(ActivityNotFoundException::class)
private fun navigateToMap(
    context: Context,
    dateTimeFormatter: DateTimeFormatter,
    coordinates: Coordinates?,
    timestamp: Instant?
) {
    if (coordinates == null || timestamp == null) return
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDateTime = dateTimeFormatter.formatDateTime(localDateTime)
    val label = context.getString(R.string.location_launch_label, formattedDateTime)
    val geoUri = with(coordinates) {
        Uri.Builder()
            .scheme("geo")
            .path("$latitude,$longitude")
            .appendQueryParameter("q", "$latitude,$longitude($label)")
            .build()
    }
    context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
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
                horizontalAccuracy = null,
                bearing = null,
                bearingAccuracy = null,
                altitude = null,
                altitudeAccuracy = null,
                speed = null,
                speedAccuracy = null,
                timestamp = null,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {}
        ) {}
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
                horizontalAccuracy = null,
                bearing = null,
                bearingAccuracy = null,
                altitude = null,
                altitudeAccuracy = null,
                speed = null,
                speedAccuracy = null,
                timestamp = null,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {}
        ) {}
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
                horizontalAccuracy = Distance.Meters(123.45678f),
                bearing = Angle.Degrees(123.45678f),
                bearingAccuracy = Angle.Degrees(123.45678f),
                altitude = Distance.Meters(123.45678f),
                altitudeAccuracy = Distance.Meters(123.45678f),
                speed = Speed.KilometersPerHour(123.45678f),
                speedAccuracy = Speed.KilometersPerHour(123.45678f),
                timestamp = Instant.DISTANT_PAST,
                showAccuracies = true,
                units = Units.METRIC,
                screenLockedOn = false
            ),
            onAndroidSettingsClick = {},
            onScreenLockToggle = {}
        ) {}
    }
}
