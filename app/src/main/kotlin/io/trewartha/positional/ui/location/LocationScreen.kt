package io.trewartha.positional.ui.location

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: LocationViewModel = hiltViewModel(),
    locationPermissionState: MultiplePermissionsState
) {
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        viewModel.onPermissionsChange()
    }

    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        Surface {
            var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
            val packageName = LocalContext.current.packageName
            PermissionsRequired(
                multiplePermissionsState = locationPermissionState,
                permissionsNotGrantedContent = {
                    if (doNotShowRationale) {
                        PermissionsNotAvailableContent {
                            navController.navigateToSettings(packageName)
                        }
                    } else {
                        PermissionsNotGrantedWithoutRationaleContent(
                            onRequestPermission = {
                                locationPermissionState.launchMultiplePermissionRequest()
                            }
                        )
                    }
                },
                permissionsNotAvailableContent = {
                    PermissionsNotAvailableContent { navController.navigateToSettings(packageName) }
                }
            ) {
                LocationContent(
                    coordinatesState = viewModel.coordinatesStateFlow.collectAsState().value,
                    locationStatsState = viewModel.locationStatsStateFlow.collectAsState().value,
                    screenLockEnabled = viewModel.screenLockEnabledFlow.collectAsState().value,
                    events = viewModel.events,
                    navigateToHelp = { navController.navigate(Screen.Help.route) },
                    navigateToSettings = { navController.navigateToSettings(packageName) },
                    onCopyClick = viewModel::onCopyClick,
                    onHelpClick = viewModel::onHelpClick,
                    onScreenLockCheckedChange = viewModel::onScreenLockCheckedChange,
                    onShareClick = viewModel::onShareClick
                )
            }
        }
    }
}

@Composable
private fun LocationContent(
    coordinatesState: CoordinatesState,
    locationStatsState: LocationStatsState,
    screenLockEnabled: Boolean,
    events: Flow<LocationScreenEvent>,
    navigateToHelp: () -> Unit,
    navigateToSettings: () -> Unit,
    onCopyClick: () -> Unit,
    onHelpClick: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
) {
    val activity = LocalContext.current.activity

    val flagKeepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

    val coordinatesCopyErrorMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_error)
    val coordinatesCopySuccessBothMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_both)
    val coordinatesCopySuccessLatitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_latitude)
    val coordinatesCopySuccessLongitudeMessage =
        stringResource(R.string.location_snackbar_coordinates_copy_success_longitude)
    val coordinatesShareErrorMessage =
        stringResource(R.string.location_snackbar_coordinates_share_error)
    val screenLockedMessage =
        stringResource(R.string.location_snackbar_screen_locked)
    val screenUnlockedMessage =
        stringResource(R.string.location_snackbar_screen_unlocked)

    val snackbarHostState = remember { SnackbarHostState() }

    val window = activity?.window

    Box {
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Box(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxSize()
            ) {
                CoordinatesText(
                    coordinatesState,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareButton(onShareClick)
                CopyButton(onCopyClick)
                ScreenLockButton(screenLockEnabled, onScreenLockCheckedChange)
                HelpButton(onHelpClick)
            }
            LocationStatsColumn(locationStatsState)
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    LaunchedEffect(events) {
        events.collect {
            when (it) {
                is LocationScreenEvent.NavigateToLocationHelp ->
                    navigateToHelp()
                is LocationScreenEvent.NavigateToSettings ->
                    navigateToSettings()
                is LocationScreenEvent.RequestPermissions -> {}
                is LocationScreenEvent.ShowCoordinatesCopyErrorSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopyErrorMessage)
                is LocationScreenEvent.ShowCoordinatesCopySuccessBothSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessBothMessage)
                is LocationScreenEvent.ShowCoordinatesCopySuccessLatitudeSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessLatitudeMessage)
                is LocationScreenEvent.ShowCoordinatesCopySuccessLongitudeSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesCopySuccessLongitudeMessage)
                is LocationScreenEvent.ShowCoordinatesShareErrorSnackbar ->
                    snackbarHostState.showSnackbar(coordinatesShareErrorMessage)
                is LocationScreenEvent.ShowCoordinatesShareSheet ->
                    activity?.let { a -> showCoordinatesShareSheet(a, it) }
                is LocationScreenEvent.ShowScreenLockedSnackbar ->
                    snackbarHostState.showSnackbar(screenLockedMessage)
                is LocationScreenEvent.ShowScreenUnlockedSnackbar ->
                    snackbarHostState.showSnackbar(screenUnlockedMessage)
            }
        }
    }
    DisposableEffect(screenLockEnabled) {
        if (screenLockEnabled) window?.addFlags(flagKeepScreenOn)
        else window?.clearFlags(flagKeepScreenOn)
        onDispose { window?.clearFlags(flagKeepScreenOn) }
    }
}

private fun NavController.navigateToSettings(packageName: String) {
    val settingsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    navigate(NavDeepLinkRequest(settingsIntent))
}

@Composable
private fun PermissionsNotAvailableContent(navigateToSettings: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxHeight()
            .padding(36.dp)
    ) {
        Text(
            stringResource(R.string.location_permission_unavailable_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            stringResource(R.string.location_permission_unavailable_message),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = { navigateToSettings() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.location_permission_unavailable_button_navigate_to_settings))
        }
    }
}

@Composable
private fun PermissionsNotGrantedWithRationaleContent(
    onRequestPermission: () -> Unit,
    onPreventFutureRequests: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.location_permission_with_rationale_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.location_permission_with_rationale_message),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.location_permission_with_rationale_button_request_permission))
        }
        OutlinedButton(
            onClick = onPreventFutureRequests,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.location_permission_with_rationale_button_prevent_future_requests))
        }
    }
}

@Composable
private fun PermissionsNotGrantedWithoutRationaleContent(onRequestPermission: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.location_permission_without_rationale_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.location_permission_without_rationale_message),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.location_permission_without_rationale_button_request_permission))
        }
    }
}

private fun showCoordinatesShareSheet(
    context: Context,
    event: LocationScreenEvent.ShowCoordinatesShareSheet
) {
    startActivity(
        context,
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, event.coordinates)
            type = "text/plain"
        },
        null
    )
}

private val Context.activity: AppCompatActivity?
    get() {
        return when (this) {
            is AppCompatActivity -> this
            is ContextWrapper -> baseContext.activity
            else -> null
        }
    }

@Preview(name = "Permissions granted", showBackground = true)
@Composable
fun LocationContentPermissionsGrantedPreview() {
    PositionalTheme(useDarkTheme = true) {
        LocationContent(
            coordinatesState = CoordinatesState("123.456789\n123.456789", 2),
            locationStatsState = LocationStatsState(
                accuracy = "123.4",
                bearing = "123.4",
                bearingAccuracy = "± 56.7",
                elevation = "123.4",
                elevationAccuracy = "± 56.7",
                speed = "123.4",
                speedAccuracy = "± 56.7",
                showAccuracies = true,
                updatedAt = "12:00:00 PM"
            ),
            screenLockEnabled = true,
            events = emptyFlow(),
            onCopyClick = {},
            onHelpClick = {},
            onScreenLockCheckedChange = {},
            onShareClick = {},
            navigateToHelp = {},
            navigateToSettings = {},
        )
    }
}