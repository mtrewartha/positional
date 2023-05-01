package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StatsColumn(
    state: LocationState?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val location = state?.location
        val units = state?.units
        val showAccuracies = state?.showAccuracies
        val placeholdersVisible = state?.location == null
        AccuracyRow(
            horizontalAccuracyMeters = location?.horizontalAccuracyMeters,
            units = units,
            showAccuracies = showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        BearingRow(
            bearingDegrees = location?.bearingDegrees,
            bearingAccuracyDegrees = location?.bearingAccuracyDegrees,
            showAccuracies = showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        AltitudeRow(
            altitudeMeters = location?.altitudeMeters,
            altitudeAccuracyMeters = location?.altitudeAccuracyMeters,
            units = units,
            showAccuracies = showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
        SpeedRow(
            speedMetersPerSecond = location?.speedMetersPerSecond,
            speedAccuracyMetersPerSecond = location?.speedAccuracyMetersPerSecond,
            units = units,
            showAccuracies = showAccuracies,
            placeholdersVisible = placeholdersVisible
        )
    }
}

@Composable
private fun AccuracyRow(
    horizontalAccuracyMeters: Float?,
    units: Units?,
    showAccuracies: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Adjust,
        name = stringResource(R.string.location_label_accuracy),
        value = if (horizontalAccuracyMeters == null || units == null) {
            null
        } else {
            stringResource(
                when (units) {
                    Units.METRIC -> R.string.location_horizontal_accuracy_metric
                    Units.IMPERIAL -> R.string.location_horizontal_accuracy_imperial
                },
                horizontalAccuracyMeters
            )
        },
        accuracy = null,
        showAccuracy = showAccuracies ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun AltitudeRow(
    altitudeMeters: Double?,
    altitudeAccuracyMeters: Float?,
    units: Units?,
    showAccuracies: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Height,
        name = stringResource(R.string.location_label_altitude),
        value = if (altitudeMeters == null || units == null) {
            null
        } else {
            stringResource(
                when (units) {
                    Units.METRIC -> R.string.location_altitude_metric
                    Units.IMPERIAL -> R.string.location_altitude_imperial
                },
                altitudeMeters
            )
        },
        accuracy = if (altitudeAccuracyMeters == null || units == null) {
            null
        } else {
            stringResource(
                when (units) {
                    Units.METRIC -> R.string.location_accuracy_metric
                    Units.IMPERIAL -> R.string.location_accuracy_imperial
                },
                altitudeAccuracyMeters
            )
        },
        showAccuracy = showAccuracies ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun BearingRow(
    bearingDegrees: Float?,
    bearingAccuracyDegrees: Float?,
    showAccuracies: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Explore,
        name = stringResource(R.string.location_label_bearing),
        value = if (bearingDegrees == null) {
            null
        } else {
            stringResource(R.string.location_bearing, bearingDegrees)
        },
        accuracy = if (bearingAccuracyDegrees == null) {
            null
        } else {
            stringResource(R.string.location_bearing_accuracy, bearingAccuracyDegrees)
        },
        showAccuracy = showAccuracies ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@Composable
private fun SpeedRow(
    speedMetersPerSecond: Float?,
    speedAccuracyMetersPerSecond: Float?,
    units: Units?,
    showAccuracies: Boolean?,
    placeholdersVisible: Boolean,
    modifier: Modifier = Modifier
) {
    StatRow(
        icon = Icons.Rounded.Speed,
        name = stringResource(R.string.location_label_speed),
        value = if (speedMetersPerSecond == null || units == null) {
            null
        } else {
            stringResource(
                when (units) {
                    Units.METRIC -> R.string.location_speed_metric
                    Units.IMPERIAL -> R.string.location_speed_imperial
                },
                speedMetersPerSecond
            )
        },
        accuracy = if (speedAccuracyMetersPerSecond == null || units == null) {
            null
        } else {
            stringResource(
                when (units) {
                    Units.METRIC -> R.string.location_accuracy_metric
                    Units.IMPERIAL -> R.string.location_accuracy_imperial
                },
                speedAccuracyMetersPerSecond
            )
        },
        showAccuracy = showAccuracies ?: false,
        showPlaceholder = placeholdersVisible,
        modifier = modifier
    )
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            StatsColumn(
                LocationState(
                    location = Location(
                        latitude = 123.456789,
                        longitude = 234.567890,
                        horizontalAccuracyMeters = 3456.789f,
                        bearingDegrees = 123.456f,
                        bearingAccuracyDegrees = 1.23f,
                        altitudeMeters = 12345.678,
                        altitudeAccuracyMeters = 123.456f,
                        speedMetersPerSecond = 123.456f,
                        speedAccuracyMetersPerSecond = 12.345f,
                        timestamp = Clock.System.now(),
                        magneticDeclinationDegrees = 12.345f
                    ),
                    coordinatesFormat = CoordinatesFormat.DD,
                    units = Units.METRIC,
                    showAccuracies = true
                )
            )
        }
    }
}
