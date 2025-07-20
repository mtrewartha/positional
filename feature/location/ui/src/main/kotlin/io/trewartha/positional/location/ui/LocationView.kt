package io.trewartha.positional.location.ui

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
import io.trewartha.positional.core.measurement.Coordinates
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.measurement.kph
import io.trewartha.positional.core.measurement.meters
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.R as CoreR
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.core.ui.locals.LocalLocale
import io.trewartha.positional.location.Location
import io.trewartha.positional.location.ui.format.CoordinatesFormatter
import io.trewartha.positional.location.ui.format.DecimalDegreesFormatter
import io.trewartha.positional.location.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import kotlin.time.Instant

@Composable
fun LocationView(
    locationState: State<Location, Unit>,
    settingsState: State<Settings, Unit>,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onShareClick: (Coordinates?) -> Unit,
    onHelpClick: () -> Unit,
) {
    // Snackbars
    val coroutineScope = rememberCoroutineScope()
    val coordinatesCopiedMessage = stringResource(R.string.ui_location_snackbar_coordinates_copied)
    var showMapError by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val coordinatesFormatter = LocalCoordinatesFormatter.current
    val dateTimeFormatter = LocalDateTimeFormatter.current

    LocationPermissionGrantedContent(
        locationState,
        settingsState,
        snackbarHostState,
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
            .padding(dimensionResource(CoreR.dimen.core_ui_standard_padding))
            .verticalScroll(rememberScrollState())
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    )
    if (showMapError) MapErrorDialog(onDismissRequest = { showMapError = false })
}

@Composable
private fun MapErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.ui_location_dialog_map_error_button_confirm))
            }
        },
        icon = { Icon(Icons.Rounded.ErrorOutline, contentDescription = null) },
        title = { Text(stringResource(R.string.ui_location_dialog_map_error_title)) },
        text = { Text(stringResource(R.string.ui_location_dialog_map_error_text)) }
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
    val label = context.getString(R.string.ui_location_launch_label, formattedDateTime)
    val geoUri = with(coordinates.asGeodeticCoordinates()) {
        Uri.Builder()
            .scheme("geo")
            .path("$latitude,$longitude")
            .appendQueryParameter("q", "$latitude,$longitude($label)")
            .build()
    }
    context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
}

@PreviewLightDark
@PreviewScreenSizes
@Preview
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        val context = LocalContext.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
        ) {
            Surface {
                LocationView(
                    locationState = State.Loading,
                    settingsState = State.Loading,
                    contentPadding = PaddingValues(),
                    snackbarHostState = SnackbarHostState(),
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
private fun LoadedPreview() {
    PositionalTheme {
        val context = LocalContext.current
        val locale = LocalLocale.current
        CompositionLocalProvider(
            LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
        ) {
            Surface {
                LocationView(
                    locationState = State.Loaded(
                        Location(
                            timestamp = Instant.DISTANT_PAST,
                            coordinates = GeodeticCoordinates(12.34567.degrees, 123.45678.degrees),
                            horizontalAccuracy = 123.45678.meters,
                            bearing = 123.45678.degrees,
                            bearingAccuracy = 123.45678.degrees,
                            altitude = 123.45678.meters,
                            altitudeAccuracy = 123.45678.meters,
                            magneticDeclination = 1.degrees,
                            speed = 123.45678.kph,
                            speedAccuracy = 123.45678.kph,
                        )
                    ),
                    settingsState = State.Loaded(
                        Settings(
                            CoordinatesFormat.DD,
                            Units.METRIC,
                            LocationAccuracyVisibility.SHOW
                        )
                    ),
                    contentPadding = PaddingValues(),
                    snackbarHostState = SnackbarHostState(),
                    onShareClick = {},
                    onHelpClick = {}
                )
            }
        }
    }
}
