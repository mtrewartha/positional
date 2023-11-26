package io.trewartha.positional.ui.compass

import android.content.Context
import android.hardware.SensorManager
import android.hardware.SensorManager.getOrientation
import android.hardware.SensorManager.remapCoordinateSystem
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Info
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.CompassAccuracy
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.utils.placeholder

fun NavGraphBuilder.compassView() {
    composable(Compass.route) {
        val viewModel: CompassViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        CompassView(state = state)
    }
}

@Composable
private fun CompassView(state: CompassViewModel.State) {
    var showInfoSheet by rememberSaveable { mutableStateOf(false) }
    var showMissingSensorDialog by remember { mutableStateOf(false) }
    if (showMissingSensorDialog) {
        AlertDialog(
            onDismissRequest = { showMissingSensorDialog = false },
            confirmButton = {
                TextButton(onClick = { showMissingSensorDialog = false }) {
                    Text(stringResource(R.string.compass_missing_hardware_dialog_confirm))
                }
            },
            text = {
                Text(
                    stringResource(R.string.compass_missing_hardware_dialog_text),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.standard_padding)),
    ) {
        when (state) {
            is CompassViewModel.State.SensorsMissing ->
                SensorsMissingContent(
                    onWhyClick = { showMissingSensorDialog = !showMissingSensorDialog },
                )
            is CompassViewModel.State.SensorsPresent ->
                SensorsPresentContent(
                    state,
                    onInfoClick = { showInfoSheet = true },
                    Modifier.fillMaxSize()
                )
        }
    }
    if (showInfoSheet) CompassInfoSheet(onDismissRequest = { showInfoSheet = false })
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
                text = stringResource(R.string.compass_missing_hardware_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.compass_missing_hardware_body),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onWhyClick) {
                Text(text = stringResource(id = R.string.compass_missing_hardware_button_why))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SensorsPresentContent(
    state: CompassViewModel.State.SensorsPresent,
    onInfoClick: () -> Unit,
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
        val remappedRotationMatrix = remember { FloatArray(ROTATION_MATRIX_SIZE) }
        val orientation = remember { FloatArray(ORIENTATION_VECTOR_SIZE) }
        val displayRotation = LocalContext.current.display?.rotation
        val windowManager =
            LocalContext.current.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val declination = when (state) {
            is CompassViewModel.State.SensorsPresent.Loaded ->
                state.magneticDeclination.inDegrees().value
            else ->
                null
        }
        val azimuthDegrees = remember(state) {
            if (state is CompassViewModel.State.SensorsPresent.Loaded) {
                val (newXAxis, newYAxis) = when (
                    val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        displayRotation ?: Surface.ROTATION_0
                    } else {
                        @Suppress("DEPRECATION")
                        windowManager
                            .defaultDisplay
                            .rotation
                    }
                ) {
                    Surface.ROTATION_0 -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> error("Unexpected rotation: $rotation")
                }
                remapCoordinateSystem(
                    state.rotationMatrix,
                    newXAxis,
                    newYAxis,
                    remappedRotationMatrix
                )
                getOrientation(remappedRotationMatrix, orientation)
                val degreesToMagneticNorth =
                    (Math.toDegrees(orientation[0].toDouble())
                        .toFloat() + DEGREES_360) % DEGREES_360
                when (state.mode) {
                    CompassMode.MAGNETIC_NORTH -> degreesToMagneticNorth
                    CompassMode.TRUE_NORTH -> degreesToMagneticNorth - (declination ?: 0f)
                }
            } else {
                null
            }
        }
        Compass(
            azimuthDegrees,
            Modifier
                .sizeIn(maxWidth = 480.dp, maxHeight = 480.dp)
                .weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeclinationText(declination)
            InfoIconButton(onInfoClick)
        }
    }
}

@Composable
private fun InfoIconButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            Icons.Rounded.Info,
            stringResource(R.string.compass_button_info_content_description),
        )
    }
}

@Composable
private fun AccuracyHelpDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(imageVector = Icons.Rounded.Warning, contentDescription = null) },
        title = { Text(stringResource(R.string.compass_dialog_accuracy_help_title)) },
        text = { Text(stringResource(R.string.compass_dialog_accuracy_help_text)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.common_ok)) }
        },
    )
}

@Composable
private fun DeclinationText(declination: Float?, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.compass_declination, declination ?: 0f),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        modifier = modifier.placeholder(declination == null)
    )
}

private const val DEGREES_360 = 360f
private const val ROTATION_MATRIX_SIZE = 9
private const val ORIENTATION_VECTOR_SIZE = 3

@PreviewLightDark
@Composable
private fun SensorsPresentLoadingPreview() {
    PositionalTheme {
        Surface {
            CompassView(state = CompassViewModel.State.SensorsPresent.Loading)
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
            CompassView(
                state = CompassViewModel.State.SensorsPresent.Loaded(
                    rotationMatrix = FloatArray(9).apply {
                        // Set the identity rotation matrix
                        set(0, 1f)
                        set(4, 1f)
                        set(8, 1f)
                    },
                    accelerometerAccuracy = CompassAccuracy.HIGH,
                    magnetometerAccuracy = null,
                    magneticDeclination = Angle.Degrees(5f),
                    mode = CompassMode.TRUE_NORTH,
                ),
            )
        }
    }
}
