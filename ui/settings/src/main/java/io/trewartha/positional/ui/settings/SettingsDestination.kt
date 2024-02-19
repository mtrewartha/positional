package io.trewartha.positional.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.ui.core.nav.NavDestination
import io.trewartha.positional.ui.core.nav.bottomNavEnterTransition
import io.trewartha.positional.ui.core.nav.bottomNavExitTransition
import io.trewartha.positional.ui.core.nav.bottomNavPopEnterTransition
import io.trewartha.positional.ui.core.nav.bottomNavPopExitTransition

data object SettingsDestination : NavDestination.MainNavDestination {

    override val route = "settings"
    override val navIcon = Icons.Rounded.Settings
    override val navLabelRes = R.string.ui_settings_title

    override fun NavGraphBuilder.composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        composable(
            SettingsDestination.route,
            enterTransition = bottomNavEnterTransition(),
            exitTransition = bottomNavExitTransition(),
            popEnterTransition = bottomNavPopEnterTransition(),
            popExitTransition = bottomNavPopExitTransition()
        ) {
            val context = LocalContext.current
            val viewModel: SettingsViewModel = hiltViewModel()
            val compassMode by viewModel.compassMode.collectAsStateWithLifecycle()
            val compassNorthVibration by viewModel.compassNorthVibration
                .collectAsStateWithLifecycle()
            val coordinatesFormat by viewModel.coordinatesFormat.collectAsStateWithLifecycle()
            val locationAccuracyVisibility by viewModel.locationAccuracyVisibility
                .collectAsStateWithLifecycle()
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val units by viewModel.units.collectAsStateWithLifecycle()
            SettingsView(
                compassMode = compassMode,
                onCompassModeChange = viewModel::onCompassModeChange,
                compassNorthVibration = compassNorthVibration,
                onCompassNorthVibrationChange = viewModel::onCompassNorthVibrationChange,
                coordinatesFormat = coordinatesFormat,
                onCoordinatesFormatChange = viewModel::onCoordinatesFormatChange,
                locationAccuracyVisibility = locationAccuracyVisibility,
                onLocationAccuracyVisibilityChange = viewModel::onLocationAccuracyVisibilityChange,
                theme = theme,
                onThemeChange = viewModel::onThemeChange,
                units = units,
                contentPadding = contentPadding,
                onUnitsChange = viewModel::onUnitsChange,
                onLicenseClick = { navigateToLicense(context) },
                onPrivacyPolicyClick = { navigateToPrivacyPolicy(context) }
            )
        }
    }
}

private fun navigateToLicense(context: Context) {
    val licenseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(LICENSE_URI))
    ContextCompat.startActivity(context, licenseIntent, null)
}

private fun navigateToPrivacyPolicy(context: Context) {
    val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
    ContextCompat.startActivity(context, privacyPolicyIntent, null)
}

private const val LICENSE_URI =
    "https://github.com/mtrewartha/positional/blob/main/LICENSE"
private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/main/PRIVACY.md"
