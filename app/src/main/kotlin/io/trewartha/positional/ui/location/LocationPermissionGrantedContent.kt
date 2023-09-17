package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.CompositionLocalProvider
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
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
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
    BoxWithConstraints {
        if (maxWidth >= maxHeight) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoordinatesView(
                    state = state,
                    onCopyClick = onCopyClick,
                    onMapClick = onMapClick,
                    onShareClick = onShareClick,
                    modifier = Modifier.weight(1f, fill = true)
                )
                StatsView(
                    state = state,
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
        } else {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CoordinatesView(
                    state = state,
                    onCopyClick = onCopyClick,
                    onMapClick = onMapClick,
                    onShareClick = onShareClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                )
                StatsView(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CoordinatesView(
    state: LocationState,
    onCopyClick: (Coordinates?) -> Unit,
    onMapClick: (Coordinates?, Instant?) -> Unit,
    onShareClick: (Coordinates?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Coordinates(coordinates = state.coordinates)
        UpdatedAtText(timestamp = state.timestamp)
        ButtonRow(
            coordinates = state.coordinates,
            timestamp = state.timestamp,
            onCopyClick = onCopyClick,
            onMapClick = onMapClick,
            onShareClick = onShareClick
        )
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

@PreviewLightDark
@PreviewScreenSizes
@Preview
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
                    state = LocationState(
                        coordinates = Coordinates(latitude = 123.456789, longitude = 123.456789),
                        coordinatesFormat = CoordinatesFormat.DD,
                        horizontalAccuracy = Distance.Meters(123.45678f),
                        bearing = Angle.Degrees(123.45678f),
                        bearingAccuracy = Angle.Degrees(123.45678f),
                        altitude = Distance.Meters(123.45678f),
                        altitudeAccuracy = Distance.Meters(123.45678f),
                        speed = Speed.KilometersPerHour(123.45678f),
                        speedAccuracy = Speed.KilometersPerHour(123.45678f),
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
}
