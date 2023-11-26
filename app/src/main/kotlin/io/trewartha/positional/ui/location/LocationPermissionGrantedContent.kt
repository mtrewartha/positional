package io.trewartha.positional.ui.location

import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.ui.IconToggleButton
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.activity
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun LocationPermissionGrantedContent(
    location: Location?,
    showAccuracies: Boolean?,
    units: Units?,
    snackbarHostState: SnackbarHostState,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholdersVisible = location == null || showAccuracies == null || units == null
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            alignment = Alignment.CenterVertically
        ),
    ) {
        CoordinatesView(
            coordinates = location?.coordinates,
            timestamp = location?.timestamp,
            snackbarHostState = snackbarHostState,
            onCopyClick = onCopyClick,
            onMapClick = onMapClick,
            onShareClick = onShareClick,
            onHelpClick = onHelpClick
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AccuracyBlock(
                accuracy = location?.horizontalAccuracy,
                units = units,
                showAccuracy = showAccuracies,
                placeholdersVisible = placeholdersVisible,
                modifier = Modifier.weight(1f)
            )
            AltitudeBlock(
                altitude = location?.altitude,
                accuracy = location?.altitudeAccuracy,
                units = units,
                showAccuracy = showAccuracies,
                placeholdersVisible = placeholdersVisible,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SpeedBlock(
                speed = location?.speed,
                accuracy = location?.speedAccuracy,
                units = units,
                showAccuracy = showAccuracies,
                placeholdersVisible = placeholdersVisible,
                modifier = Modifier.weight(1f)
            )
            BearingBlock(
                bearing = location?.bearing,
                accuracy = location?.bearingAccuracy,
                showAccuracy = showAccuracies,
                placeholdersVisible = placeholdersVisible,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ButtonRow(
    coordinates: Coordinates?,
    timestamp: Instant?,
    snackbarHostState: SnackbarHostState,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MapButton(
            onClick = { onMapClick(coordinates, timestamp) },
            enabled = coordinates != null
        )
        ShareButton(onClick = { onShareClick(coordinates) }, enabled = coordinates != null)
        CopyButton(onClick = { onCopyClick(coordinates) }, enabled = coordinates != null)
        ScreenLockToggleButton(snackbarHostState)
        HelpButton(onHelpClick)
    }
}

@Composable
private fun Coordinates(
    coordinates: Coordinates?,
    modifier: Modifier = Modifier
) {
    val formattedCoordinates = LocalCoordinatesFormatter.current.formatForDisplay(coordinates)
    val maxCoordinateLength = formattedCoordinates.maxOf { it?.length ?: 0 }
    val joinedPaddedCoordinateLines = formattedCoordinates.joinToString("\n") { coordinate ->
        coordinate.orEmpty().padStart(maxCoordinateLength)
    }
    AutoShrinkingText(
        text = joinedPaddedCoordinateLines,
        modifier = modifier.placeholder(visible = coordinates == null),
        textAlign = TextAlign.Center,
        maxLines = formattedCoordinates.size,
        style = MaterialTheme.typography.displayLarge,
    )
}

@Composable
private fun CoordinatesView(
    coordinates: Coordinates?,
    timestamp: Instant?,
    snackbarHostState: SnackbarHostState,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Coordinates(coordinates = coordinates)
        UpdatedAtText(timestamp = timestamp)
        ButtonRow(
            coordinates = coordinates,
            timestamp = timestamp,
            snackbarHostState = snackbarHostState,
            onCopyClick = onCopyClick,
            onMapClick = onMapClick,
            onShareClick = onShareClick,
            onHelpClick = onHelpClick
        )
    }
}

@Composable
private fun CopyButton(onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            Icons.Rounded.FileCopy,
            stringResource(R.string.location_coordinates_copy_content_description),
        )
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
            contentDescription = stringResource(R.string.location_button_help_content_description),
        )
    }
}

@Composable
private fun MapButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            Icons.Rounded.Map,
            stringResource(R.string.location_button_map_content_description),
        )
    }
}

@Composable
private fun ScreenLockToggleButton(snackbarHostState: SnackbarHostState) {
    val window = LocalContext.current.activity?.window ?: return
    var screenLockedOn by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val screenLockMessage = stringResource(
        // This might look backwards, but it's not. We're setting the message to show *when the
        // state changes from the current value*, not the message to show *for* the current value.
        if (screenLockedOn) {
            R.string.location_snackbar_screen_unlocked
        } else {
            R.string.location_snackbar_screen_locked
        }
    )
    DisposableEffect(screenLockedOn) {
        if (screenLockedOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    IconToggleButton(
        checked = screenLockedOn,
        onCheckedChange = {
            screenLockedOn = it
            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(screenLockMessage)
            }
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.ScreenLockPortrait,
            contentDescription = stringResource(
                if (screenLockedOn) {
                    R.string.location_button_lock_content_description_on
                } else {
                    R.string.location_button_lock_content_description_off
                }
            ),
        )
    }
}

@Composable
private fun ShareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(
            Icons.Rounded.Share,
            stringResource(R.string.location_button_share_content_description),
        )
    }
}

@Composable
private fun UpdatedAtText(timestamp: Instant?, modifier: Modifier = Modifier) {
    val localTimestamp = timestamp?.toLocalDateTime(TimeZone.currentSystemDefault())?.time
    Text(
        text = localTimestamp
            ?.let {
                stringResource(
                    R.string.location_updated_at,
                    LocalDateTimeFormatter.current.formatTime(it)
                )
            }
            ?: "",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .widthIn(min = 128.dp)
            .placeholder(visible = timestamp == null)
    )
}

@PreviewLightDark
@Composable
private fun LocatingPreview() {
    PositionalTheme {
        Surface {
            val context = LocalContext.current
            val locale = LocalLocale.current
            CompositionLocalProvider(
                LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
            ) {
                LocationPermissionGrantedContent(
                    location = null,
                    showAccuracies = true,
                    units = Units.METRIC,
                    snackbarHostState = SnackbarHostState(),
                    onCopyClick = {},
                    onMapClick = { _, _ -> },
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
        Surface {
            val context = LocalContext.current
            val locale = LocalLocale.current
            CompositionLocalProvider(
                LocalCoordinatesFormatter provides DecimalDegreesFormatter(context, locale)
            ) {
                LocationPermissionGrantedContent(
                    location = Location(
                        coordinates = Coordinates(latitude = 123.456789, longitude = 123.456789),
                        horizontalAccuracy = Distance.Meters(123.45678f),
                        bearing = Angle.Degrees(123.45678f),
                        bearingAccuracy = Angle.Degrees(123.45678f),
                        altitude = Distance.Meters(123.45678f),
                        altitudeAccuracy = Distance.Meters(123.45678f),
                        magneticDeclination = Angle.Degrees(1f),
                        speed = Speed.KilometersPerHour(123.45678f),
                        speedAccuracy = Speed.KilometersPerHour(123.45678f),
                        timestamp = Instant.DISTANT_PAST,
                    ),
                    showAccuracies = true,
                    units = Units.METRIC,
                    snackbarHostState = SnackbarHostState(),
                    onCopyClick = {},
                    onMapClick = { _, _ -> },
                    onShareClick = {},
                    onHelpClick = {}
                )
            }
        }
    }
}
