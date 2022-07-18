package io.trewartha.positional.ui.compass

import android.hardware.SensorManager
import android.hardware.SensorManager.getOrientation
import android.hardware.SensorManager.remapCoordinateSystem
import android.view.Surface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.North
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.Compass
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews
import io.trewartha.positional.ui.utils.placeholder
import kotlin.math.roundToInt

@Composable
fun CompassScreen(
    viewModel: CompassViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    CompassContent(
        state = state,
        onSensorsMissingWhyClick = viewModel::onSensorsMissingWhyClick,
    )
}

@Composable
private fun CompassContent(
    state: CompassViewModel.State,
    onSensorsMissingWhyClick: () -> Unit,
) {
    Scaffold { paddingValues ->
        when (state) {
            is CompassViewModel.State.SensorsMissing ->
                SensorsMissingContent(
                    state = state,
                    onWhyClick = onSensorsMissingWhyClick,
                    modifier = Modifier.padding(paddingValues)
                )
            is CompassViewModel.State.SensorsPresent ->
                SensorsPresentContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues)
                )
        }
    }
}

@Composable
private fun SensorsMissingContent(
    state: CompassViewModel.State.SensorsMissing,
    onWhyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 296.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
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
            AnimatedVisibility(
                visible = state.detailsVisible,
                enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessVeryLow)) +
                        expandVertically(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                dampingRatio = Spring.DampingRatioLowBouncy
                            ),
                            expandFrom = Alignment.Top
                        ),
            ) {
                Text(
                    text = stringResource(id = R.string.compass_missing_hardware_body_details),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SensorsPresentContent(
    state: CompassViewModel.State.SensorsPresent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholdersVisible = state is CompassViewModel.State.SensorsPresent.Loading
        val remappedRotationMatrix = remember { FloatArray(9) }
        val orientation = remember { FloatArray(3) }
        val azimuthDegrees = if (state is CompassViewModel.State.SensorsPresent.Loaded) {
            val (newXAxis, newYAxis) = when (LocalContext.current.display?.rotation
                ?: Surface.ROTATION_0) {
                Surface.ROTATION_0 -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_MINUS_X
                Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                else -> throw IllegalStateException()
            }
            remapCoordinateSystem(state.rotationMatrix, newXAxis, newYAxis, remappedRotationMatrix)
            getOrientation(remappedRotationMatrix, orientation)
            val degreesToMagneticNorth =
                (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
            when (state.mode) {
                CompassMode.MAGNETIC_NORTH -> degreesToMagneticNorth
                CompassMode.TRUE_NORTH -> degreesToMagneticNorth - state.magneticDeclinationDegrees
            }
        } else {
            0f
        }
        Icon(
            imageVector = Icons.Rounded.North,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        ConstraintLayout(
            modifier = Modifier
                .widthIn(min = 128.dp)
                .placeholder(visible = placeholdersVisible)
        ) {
            val (degreesText, symbolText) = createRefs()
            Text(
                text = "${azimuthDegrees.roundToInt() % 360}",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.constrainAs(degreesText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            Text(
                text = "°",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.constrainAs(symbolText) {
                    top.linkTo(degreesText.top)
                    start.linkTo(degreesText.end)
                }
            )
        }
        Compass(
            azimuthDegrees = azimuthDegrees,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .placeholder(visible = placeholdersVisible)
        )
        StatsColumn(state = state)
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun SensorsMissingPreview() {
    PositionalTheme {
        Surface {
            SensorsMissingContent(
                state = CompassViewModel.State.SensorsMissing(detailsVisible = true),
                onWhyClick = {},
            )
        }
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun SensorsPresentLoadedPreview() {
    PositionalTheme {
        Surface {
            SensorsPresentContent(
                state = CompassViewModel.State.SensorsPresent.Loaded(
                    rotationMatrix = FloatArray(9).apply {
                        // Set the identity rotation matrix
                        set(0, 1f)
                        set(4, 1f)
                        set(8, 1f)
                    },
                    accelerometerAccuracy = Compass.Accuracy.HIGH,
                    magnetometerAccuracy = null,
                    magneticDeclinationDegrees = 5f,
                    mode = CompassMode.TRUE_NORTH,
                )
            )
        }
    }
}