package io.trewartha.positional.ui.location

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.R
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.bottomNavEnterTransition
import io.trewartha.positional.ui.bottomNavExitTransition
import io.trewartha.positional.ui.bottomNavPopEnterTransition
import io.trewartha.positional.ui.bottomNavPopExitTransition
import io.trewartha.positional.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
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
    navController: NavController,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onAndroidSettingsClick: () -> Unit
) {
    composable(
        NavDestination.Location.route,
        enterTransition = bottomNavEnterTransition(),
        exitTransition = bottomNavExitTransition(NavDestination.LocationHelp.route),
        popEnterTransition = bottomNavPopEnterTransition(NavDestination.LocationHelp.route),
        popExitTransition = bottomNavPopExitTransition()
    ) {
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)

        val viewModel: LocationViewModel = hiltViewModel()
        val coordinatesFormat by viewModel.coordinatesFormat.collectAsStateWithLifecycle()
        val location by viewModel.location.collectAsStateWithLifecycle()
        val showAccuracies by viewModel.showAccuracies.collectAsStateWithLifecycle()
        val units by viewModel.units.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides when (coordinatesFormat) {
                CoordinatesFormat.DD, null -> DecimalDegreesFormatter(context, locale)
                CoordinatesFormat.DDM -> DegreesDecimalMinutesFormatter(context, locale)
                CoordinatesFormat.DMS -> DegreesMinutesSecondsFormatter(context, locale)
                CoordinatesFormat.MGRS -> MgrsFormatter(context)
                CoordinatesFormat.UTM -> UtmFormatter(context, locale)
            }
        ) {
            val coordinatesFormatter = LocalCoordinatesFormatter.current
            LocationView(
                locationPermissionsState = locationPermissionsState,
                location = location,
                showAccuracies = showAccuracies,
                units = units,
                contentPadding = contentPadding,
                snackbarHostState = snackbarHostState,
                onAndroidSettingsClick = onAndroidSettingsClick,
                onShareClick = click@{ coordinates ->
                    val formattedCoordinates =
                        coordinates?.let { coordinatesFormatter.formatForCopy(it) }
                            ?: return@click
                    shareCoordinates(context, formattedCoordinates)
                },
                onHelpClick = { navController.navigate(NavDestination.LocationHelp.route) }
            )
        }
    }
}

@Composable
private fun LocationView(
    locationPermissionsState: MultiplePermissionsState,
    location: Location?,
    showAccuracies: Boolean?,
    units: Units?,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onAndroidSettingsClick: () -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    onHelpClick: () -> Unit,
) {
    // Snackbars
    val coroutineScope = rememberCoroutineScope()
    val coordinatesCopiedMessage = stringResource(R.string.location_snackbar_coordinates_copied)
    var showMapError by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    if (locationPermissionsState.allPermissionsGranted) {
        val clipboardManager = LocalClipboardManager.current
        val context = LocalContext.current
        val coordinatesFormatter = LocalCoordinatesFormatter.current
        val dateTimeFormatter = LocalDateTimeFormatter.current

        LocationPermissionGrantedContent(
            location = location,
            showAccuracies = showAccuracies,
            units = units,
            snackbarHostState = snackbarHostState,
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
            onHelpClick = onHelpClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(dimensionResource(R.dimen.standard_padding))
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    } else {
        LocationPermissionRequiredContent(
            locationPermissionsState = locationPermissionsState,
            onSettingsClick = onAndroidSettingsClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(dimensionResource(R.dimen.standard_padding))
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
    if (showMapError) MapErrorDialog(onDismissRequest = { showMapError = false })
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

private fun shareCoordinates(context: Context, formattedCoordinates: String) {
    startActivity(
        context,
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, formattedCoordinates)
            type = "text/plain"
        },
        null
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PermissionNotGrantedPreview() {
    PositionalTheme {
        Surface {
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
                location = null,
                showAccuracies = true,
                units = null,
                contentPadding = PaddingValues(),
                snackbarHostState = SnackbarHostState(),
                onAndroidSettingsClick = {},
                onShareClick = {},
                onHelpClick = {}
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Preview
@Composable
private fun LocatingPreview() {
    PositionalTheme {
        val context = LocalContext.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
        ) {
            Surface {
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
                    location = null,
                    showAccuracies = true,
                    units = null,
                    contentPadding = PaddingValues(),
                    snackbarHostState = SnackbarHostState(),
                    onAndroidSettingsClick = {},
                    onShareClick = {},
                    onHelpClick = {}
                )
            }
        }
    }
}

@PreviewFontScale
@PreviewLightDark
@PreviewScreenSizes
@Preview
@Composable
private fun LocatedPreview() {
    PositionalTheme {
        val context = LocalContext.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
        ) {
            Surface {
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
                    location = Location(
                        timestamp = Instant.DISTANT_PAST,
                        coordinates = Coordinates(
                            latitude = 123.456789,
                            longitude = 123.456789
                        ),
                        horizontalAccuracy = Distance.Meters(123.45678f),
                        bearing = Angle.Degrees(123.45678f),
                        bearingAccuracy = Angle.Degrees(123.45678f),
                        altitude = Distance.Meters(123.45678f),
                        altitudeAccuracy = Distance.Meters(123.45678f),
                        magneticDeclination = Angle.Degrees(1f),
                        speed = Speed.KilometersPerHour(123.45678f),
                        speedAccuracy = Speed.KilometersPerHour(123.45678f),
                    ),
                    showAccuracies = true,
                    units = Units.METRIC,
                    contentPadding = PaddingValues(),
                    snackbarHostState = SnackbarHostState(),
                    onAndroidSettingsClick = {},
                    onShareClick = {},
                    onHelpClick = {}
                )
            }
        }
    }
}
