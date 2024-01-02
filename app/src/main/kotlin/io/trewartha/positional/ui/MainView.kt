package io.trewartha.positional.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.NavDestination.Sun
import io.trewartha.positional.ui.compass.compassHelpView
import io.trewartha.positional.ui.compass.compassView
import io.trewartha.positional.ui.location.locationHelpView
import io.trewartha.positional.ui.location.locationView
import io.trewartha.positional.ui.settings.settingsView
import io.trewartha.positional.ui.sun.sunHelpView
import io.trewartha.positional.ui.sun.sunView
import io.trewartha.positional.ui.utils.activity

@Composable
fun MainView(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme by remember {
        derivedStateOf {
            when (theme) {
                Theme.DEVICE -> isSystemInDarkTheme
                Theme.DARK -> true
                Theme.LIGHT -> false
            }
        }
    }
    val systemBarStyle =
        SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { useDarkTheme }
    LocalContext.current.activity?.enableEdgeToEdge(
        statusBarStyle = systemBarStyle,
        navigationBarStyle = systemBarStyle
    )
    PositionalTheme(useDarkTheme = useDarkTheme) {
        val isCompactWidthWindow = windowWidthSizeClass == WindowWidthSizeClass.Compact
        val mainNavDestinations = setOf(Location, Compass, Sun, Settings)
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            bottomBar = bottomBar@{
                if (!isCompactWidthWindow) return@bottomBar
                MainNavigationBar(navHostController, mainNavDestinations)
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { contentPadding ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isCompactWidthWindow) {
                    MainNavigationRail(
                        navHostController,
                        mainNavDestinations,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                val context = LocalContext.current
                NavHost(
                    navHostController,
                    startDestination = Location.route,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    compassView(navHostController, contentPadding)
                    compassHelpView(navHostController, contentPadding)
                    locationView(
                        navHostController,
                        contentPadding,
                        snackbarHostState,
                        onAndroidSettingsClick = { navigateToSettings(context) },
                    )
                    locationHelpView(navHostController, contentPadding)
                    settingsView(
                        contentPadding,
                        onLicenseClick = { navigateToLicense(context) },
                        onPrivacyPolicyClick = { navigateToPrivacyPolicy(context) }
                    )
                    sunView(navHostController, contentPadding)
                    sunHelpView(navHostController, contentPadding)
                }
            }
        }
    }
}

@Composable
private fun MainNavigationBar(
    navHostController: NavHostController,
    mainNavDestination: Set<NavDestination.MainNavDestination>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        mainNavDestination.forEach { mainNavDestination ->
            NavigationBarItem(
                selected = currentRoute?.startsWith(mainNavDestination.route) ?: false,
                onClick = {
                    onNavigationItemClick(currentRoute, mainNavDestination, navHostController)
                },
                icon = { Icon(imageVector = mainNavDestination.navIcon, null) },
                label = { Text(stringResource(mainNavDestination.navLabelRes)) }
            )
        }
    }
}

@Composable
private fun MainNavigationRail(
    navHostController: NavHostController,
    mainNavDestination: Set<NavDestination.MainNavDestination>,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            mainNavDestination.forEach { mainNavDestination ->
                NavigationRailItem(
                    selected = currentRoute?.startsWith(mainNavDestination.route) ?: false,
                    onClick = {
                        onNavigationItemClick(currentRoute, mainNavDestination, navHostController)
                    },
                    icon = { Icon(imageVector = mainNavDestination.navIcon, null) },
                    label = { Text(stringResource(mainNavDestination.navLabelRes)) },
                )
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

private fun navigateToSettings(context: Context) {
    val settingsIntent = Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    ContextCompat.startActivity(context, settingsIntent, null)
}

private fun navigateToLicense(context: Context) {
    val licenseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(LICENSE_URI))
    ContextCompat.startActivity(context, licenseIntent, null)
}

private fun navigateToPrivacyPolicy(context: Context) {
    val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
    ContextCompat.startActivity(context, privacyPolicyIntent, null)
}

private fun onNavigationItemClick(
    currentRoute: String?,
    mainNavDestination: NavDestination.MainNavDestination,
    navHostController: NavHostController
) {
    if (currentRoute == mainNavDestination.route || currentRoute == null) return
    navHostController.navigate(mainNavDestination.route) {
        launchSingleTop = true
        popUpTo(currentRoute) { inclusive = true }
        restoreState = true
    }
}

private const val LICENSE_URI =
    "https://github.com/mtrewartha/positional/blob/main/LICENSE"
private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/main/PRIVACY.md"
