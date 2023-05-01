package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Launch
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
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
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
    locationState: LocationState?,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onShareClick: () -> Unit,
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
                coordinates = locationState?.location
                    ?.let { Coordinates(it.latitude, it.longitude) },
                format = locationState?.coordinatesFormat,
                modifier = Modifier.fillMaxWidth()
            )
            UpdatedAtText(timestamp = locationState?.location?.timestamp)
            ButtonRow(
                location = locationState?.location,
                onCopyClick = onCopyClick,
                onLaunchClick = onLaunchClick,
                onShareClick = onShareClick
            )
        }
        StatsColumn(state = locationState, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Coordinates(
    coordinates: Coordinates?,
    format: CoordinatesFormat?,
    modifier: Modifier = Modifier
) {
    val style = MaterialTheme.typography.displayLarge
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (formattedCoordinate in formattedCoordinates) {
            Text(
                text = formattedCoordinate ?: "",
                modifier = Modifier
                    .widthIn(min = 256.dp)
                    .placeholder(visible = formattedCoordinate == null),
                style = style,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ButtonRow(
    location: Location?,
    onCopyClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLaunchClick, enabled = location != null) {
            Icon(
                Icons.Rounded.Launch,
                stringResource(R.string.location_launch_button_content_description),
            )
        }
        IconButton(onClick = onShareClick, enabled = location != null) {
            Icon(
                Icons.Rounded.Share,
                stringResource(R.string.location_share_button_content_description),
            )
        }
        IconButton(onClick = onCopyClick, enabled = location != null) {
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
private fun LoadingStatePreview() {
    PositionalTheme {
        Surface {
            LocationPermissionGrantedContent(
                locationState = null,
                onCopyClick = {},
                onLaunchClick = {},
                onShareClick = {}
            )
        }
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun LocatingPreview() {
    PositionalTheme {
        Surface {
            LocationPermissionGrantedContent(
                locationState = LocationState(
                    location = null,
                    coordinatesFormat = CoordinatesFormat.DD,
                    units = Units.METRIC,
                    showAccuracies = true
                ),
                onCopyClick = {},
                onLaunchClick = {},
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
                onCopyClick = {},
                onLaunchClick = {},
                onShareClick = {}
            )
        }
    }
}
