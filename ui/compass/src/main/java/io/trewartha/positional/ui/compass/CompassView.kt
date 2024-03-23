package io.trewartha.positional.ui.compass

import io.trewartha.positional.ui.design.R as UIDesignR
import android.content.Context
import android.view.Surface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.trewartha.positional.model.compass.Azimuth
import io.trewartha.positional.model.core.measurement.Angle
import io.trewartha.positional.model.core.measurement.degrees
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.model.settings.CompassNorthVibration
import io.trewartha.positional.ui.core.State
import io.trewartha.positional.ui.design.PositionalTheme
import io.trewartha.positional.ui.design.locals.LocalVibrator
import io.trewartha.positional.ui.design.modifier.placeholder

@Composable
fun CompassView(
    state: State<CompassData, CompassError>,
    contentPadding: PaddingValues,
    onHelpClick: () -> Unit
) {
    var showMissingSensorDialog by remember { mutableStateOf(false) }
    if (showMissingSensorDialog) {
        AlertDialog(
            onDismissRequest = { showMissingSensorDialog = false },
            confirmButton = {
                TextButton(onClick = { showMissingSensorDialog = false }) {
                    Text(stringResource(R.string.ui_compass_missing_hardware_dialog_confirm))
                }
            },
            text = {
                Text(
                    stringResource(R.string.ui_compass_missing_hardware_dialog_text),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(dimensionResource(UIDesignR.dimen.ui_design_standard_padding)),
    ) {
        when (state) {
            is State.Failure ->
                SensorsMissingContent(
                    onWhyClick = { showMissingSensorDialog = !showMissingSensorDialog },
                    Modifier.fillMaxSize()
                )
            is State.Loading,
            is State.Loaded ->
                SensorsPresentContent(state, onHelpClick, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SensorsMissingContent(
    onWhyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 384.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Memory,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.ui_compass_missing_hardware_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.ui_compass_missing_hardware_body),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onWhyClick) {
                Text(text = stringResource(id = R.string.ui_compass_missing_hardware_button_why))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SensorsPresentContent(
    state: State<CompassData, *>,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterVertically),
    ) {
        var showAccuracyHelpDialog by remember { mutableStateOf(false) }
        if (showAccuracyHelpDialog) {
            AccuracyHelpDialog(onDismissRequest = { showAccuracyHelpDialog = false })
        }
        val context = LocalContext.current
        val baseAzimuth = state.dataOrNull?.let { data ->
            when (data.mode) {
                CompassMode.MAGNETIC_NORTH -> data.azimuth
                CompassMode.TRUE_NORTH -> data.declination?.let { data.azimuth + it }
            }
        }
        val adjustedAzimuth = baseAzimuth?.adjustForDisplayRotation(context)
        val northVibration = state.dataOrNull?.northVibration
        Compass(
            adjustedAzimuth?.angle,
            northVibration,
            Modifier
                .sizeIn(maxWidth = 480.dp, maxHeight = 480.dp)
                .weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val declination = state.dataOrNull?.declination?.inDegrees()?.magnitude
            DeclinationText(declination)
            HelpButton(onHelpClick)
        }
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            Icons.AutoMirrored.Rounded.HelpOutline,
            stringResource(R.string.ui_compass_button_help_content_description),
        )
    }
}

@Composable
private fun AccuracyHelpDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(imageVector = Icons.Rounded.Warning, contentDescription = null) },
        title = { Text(stringResource(R.string.ui_compass_dialog_accuracy_help_title)) },
        text = { Text(stringResource(R.string.ui_compass_dialog_accuracy_help_text)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(UIDesignR.string.ui_design_ok))
            }
        },
    )
}

@Composable
private fun DeclinationText(declination: Double?, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.ui_compass_declination, declination ?: 0f),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        modifier = modifier.placeholder(declination == null)
    )
}

private fun getDisplayRotation(context: Context): Angle =
    try {
        when (ContextCompat.getDisplayOrDefault(context).rotation) {
            Surface.ROTATION_0 -> DEGREES_0
            Surface.ROTATION_90 -> DEGREES_90
            Surface.ROTATION_180 -> DEGREES_180
            Surface.ROTATION_270 -> DEGREES_270
            else -> DEGREES_0
        }
    } catch (_: NullPointerException) { // Compose preview causes this
        DEGREES_0
    }.degrees

private fun Azimuth.adjustForDisplayRotation(context: Context): Azimuth =
    plus(getDisplayRotation(context))

@PreviewLightDark
@Composable
private fun SensorsMissingPreview() {
    PositionalTheme {
        Surface {
            CompassView(
                state = State.Failure(CompassError.SensorsMissing),
                contentPadding = PaddingValues(),
                onHelpClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SensorsPresentLoadingPreview() {
    PositionalTheme {
        Surface {
            CompassView(
                state = State.Loading,
                contentPadding = PaddingValues(),
                onHelpClick = {}
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
private fun SensorsPresentLoadedPreview() {
    PositionalTheme {
        Surface {
            CompositionLocalProvider(LocalVibrator provides null) {
                CompassView(
                    state = State.Loaded(
                        CompassData(
                            azimuth = Azimuth(45.degrees),
                            declination = 1.degrees,
                            mode = CompassMode.TRUE_NORTH,
                            northVibration = CompassNorthVibration.SHORT
                        )
                    ),
                    contentPadding = PaddingValues(),
                    onHelpClick = {}
                )
            }
        }
    }
}

private const val DEGREES_0 = 0f
private const val DEGREES_90 = 90f
private const val DEGREES_180 = 180f
private const val DEGREES_270 = 270f
