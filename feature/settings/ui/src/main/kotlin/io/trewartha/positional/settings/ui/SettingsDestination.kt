package io.trewartha.positional.settings.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.core.ui.nav.NavDestination
import io.trewartha.positional.core.ui.nav.bottomNavEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavExitTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopExitTransition

public data object SettingsDestination : NavDestination.MainNavDestination {

    override val route: String = "settings"
    override val navIcon: ImageVector = Icons.Rounded.Settings
    override val navLabelRes: Int = R.string.feature_settings_ui_title

    context(navGraphBuilder: NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        navGraphBuilder.composable(
            route,
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
    val licenseIntent = Intent(Intent.ACTION_VIEW, LICENSE_URI.toUri())
    ContextCompat.startActivity(context, licenseIntent, null)
}

private fun navigateToPrivacyPolicy(context: Context) {
    val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URI.toUri())
    ContextCompat.startActivity(context, privacyPolicyIntent, null)
}

private const val LICENSE_URI =
    "https://github.com/mtrewartha/positional/blob/main/LICENSE"
private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/main/PRIVACY.md"
