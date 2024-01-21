package io.trewartha.positional.ui.compass

import android.content.Context
import android.os.Vibrator
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.Azimuth
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.ui.CompassNorthVibration
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.State
import io.trewartha.positional.ui.bottomNavEnterTransition
import io.trewartha.positional.ui.bottomNavExitTransition
import io.trewartha.positional.ui.bottomNavPopEnterTransition
import io.trewartha.positional.ui.bottomNavPopExitTransition
import io.trewartha.positional.ui.locals.LocalVibrator
import io.trewartha.positional.ui.utils.placeholder

fun NavGraphBuilder.compassView(navController: NavController, contentPadding: PaddingValues) {
    composable(
        NavDestination.Compass.route,
        enterTransition = bottomNavEnterTransition(),
        exitTransition = bottomNavExitTransition(NavDestination.CompassHelp.route),
        popEnterTransition = bottomNavPopEnterTransition(NavDestination.CompassHelp.route),
        popExitTransition = bottomNavPopExitTransition()
    ) {
        val viewModel: CompassViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        CompositionLocalProvider(
            LocalVibrator provides
                    @Suppress("DEPRECATION") // It matches our needs and goes back pre API 21
                    LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        ) {
            CompassView(
                state = state,
                contentPadding = contentPadding,
                onHelpClick = { navController.navigate(NavDestination.CompassHelp.route) })
        }
    }
}

@Composable
private fun CompassView(
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
            .padding(contentPadding)
            .padding(dimensionResource(R.dimen.standard_padding)),
    ) {
        when (state) {
            is State.Error ->
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
                CompassMode.MAGNETIC_NORTH -> data.azimuth.angle
                CompassMode.TRUE_NORTH -> data.declination?.let { data.azimuth.angle + it }
            }
        }
        val adjustedAzimuth = baseAzimuth?.let { adjustAzimuthForDisplayRotation(context, it) }
        val northVibration = state.dataOrNull?.northVibration
        Compass(
            adjustedAzimuth,
            northVibration,
            Modifier
                .sizeIn(maxWidth = 480.dp, maxHeight = 480.dp)
                .weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val declination = state.dataOrNull?.declination?.inDegrees()?.value
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
            stringResource(R.string.compass_button_help_content_description),
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

private fun adjustAzimuthForDisplayRotation(context: Context, baseAzimuth: Angle): Angle =
    baseAzimuth.plus(getDisplayRotation(context))

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
    }.let { Angle.Degrees(it) }

@PreviewLightDark
@Composable
private fun SensorsMissingPreview() {
    PositionalTheme {
        Surface {
            CompassView(
                state = State.Error(CompassError.SensorsMissing),
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
            CompassView(
                state = State.Loaded(
                    CompassData(
                        azimuth = Azimuth(Angle.Degrees(45f)),
                        declination = Angle.Degrees(1f),
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

private const val DEGREES_0 = 0f
private const val DEGREES_90 = 90f
private const val DEGREES_180 = 180f
private const val DEGREES_270 = 270f
