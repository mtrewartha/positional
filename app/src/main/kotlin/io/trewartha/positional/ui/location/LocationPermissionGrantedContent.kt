package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesDecimalMinutesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesMinutesSecondsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.MgrsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.UtmFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun LocationPermissionGrantedContent(
    state: LocationState,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Coordinates(
                coordinates = state.coordinates,
                format = state.coordinatesFormat,
                modifier = Modifier.fillMaxWidth()
            )
            UpdatedAtText(timestamp = state.timestamp)
            ButtonRow(
                coordinates = state.coordinates,
                timestamp = state.timestamp,
                onCopyClick = onCopyClick,
                onMapClick = onMapClick,
                onShareClick = onShareClick
            )
        }
        StatsColumn(
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Coordinates(
    coordinates: Coordinates?,
    format: CoordinatesFormat?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locale = LocalLocale.current
    val formatter = remember(format) {
        when (format) {
            CoordinatesFormat.DD, null -> DecimalDegreesFormatter(context, locale)
            CoordinatesFormat.DDM -> DegreesDecimalMinutesFormatter(context, locale)
            CoordinatesFormat.DMS -> DegreesMinutesSecondsFormatter(context, locale)
            CoordinatesFormat.MGRS -> MgrsFormatter(context)
            CoordinatesFormat.UTM -> UtmFormatter(context, locale)
        }
    }
    val formattedCoordinates = formatter.formatForDisplay(coordinates)
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
private fun ButtonRow(
    coordinates: Coordinates?,
    timestamp: Instant?,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onMapClick(coordinates, timestamp) },
            enabled = coordinates != null
        ) {
            Icon(
                Icons.Rounded.Map,
                stringResource(R.string.location_button_map_content_description),
            )
        }
        IconButton(
            onClick = { onShareClick(coordinates) },
            enabled = coordinates != null
        ) {
            Icon(
                Icons.Rounded.Share,
                stringResource(R.string.location_button_share_content_description),
            )
        }
        IconButton(
            onClick = { onCopyClick(coordinates) },
            enabled = coordinates != null
        ) {
            Icon(
                Icons.Rounded.FileCopy,
                stringResource(R.string.location_coordinates_copy_content_description),
            )
        }
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
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .widthIn(min = 128.dp)
            .placeholder(visible = timestamp == null)
    )
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LocatingPreview() {
    PositionalTheme {
        Surface {
            LocationPermissionGrantedContent(
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
                    units = Units.METRIC,
                    showAccuracies = true,
                    screenLockedOn = false
                ),
                onCopyClick = {},
                onMapClick = { _, _ -> },
                onShareClick = {}
            )
        }
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LocatedPreview() {
    PositionalTheme {
        Surface {
            LocationPermissionGrantedContent(
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
                    units = Units.METRIC,
                    showAccuracies = true,
                    screenLockedOn = false
                ),
                onCopyClick = {},
                onMapClick = { _, _ -> },
                onShareClick = {}
            )
        }
    }
}
