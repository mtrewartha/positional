package io.trewartha.positional

import android.Manifest
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.compass.ui.CompassDestination
import io.trewartha.positional.compass.ui.CompassHelpDestination
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.activity
import io.trewartha.positional.core.ui.nav.NavDestination
import io.trewartha.positional.location.ui.LocationDestination
import io.trewartha.positional.location.ui.LocationHelpDestination
import io.trewartha.positional.location.ui.LocationPermissionRequiredContent
import io.trewartha.positional.settings.Theme
import io.trewartha.positional.settings.ui.SettingsDestination
import io.trewartha.positional.settings.ui.SettingsViewModel
import io.trewartha.positional.sun.ui.SunDestination
import io.trewartha.positional.sun.ui.SunHelpDestination

@Composable
public fun MainView(
    viewModel: SettingsViewModel = hiltViewModel(),
    navHostController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme by remember(theme) {
        derivedStateOf {
            when (theme) {
                Theme.DEVICE -> isSystemInDarkTheme
                Theme.DARK -> true
                Theme.LIGHT -> false
                null -> false
            }
        }
    }
    val systemBarStyle =
        SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { useDarkTheme }
    LocalContext.current.activity?.enableEdgeToEdge(systemBarStyle, systemBarStyle)
    PositionalTheme(useDarkTheme = useDarkTheme) {
        val context = LocalContext.current
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
        val isCompactWidthWindow = windowWidthSizeClass == WindowWidthSizeClass.Compact
        val mainNavDestinations =
            setOf(LocationDestination, CompassDestination, SunDestination, SettingsDestination)
        val snackbarHostState = remember { SnackbarHostState() }
        if (locationPermissionsState.allPermissionsGranted) {
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
                    NavHost(
                        navHostController,
                        startDestination = LocationDestination.route,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        with(CompassDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(CompassHelpDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(LocationDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(LocationHelpDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(SettingsDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(SunDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                        with(SunHelpDestination) {
                            composable(navHostController, snackbarHostState, contentPadding)
                        }
                    }
                }
            }
        } else {
            LocationPermissionRequiredContent(
                locationPermissionsState,
                onSettingsClick = { navigateToSettings(context) },
                Modifier.fillMaxSize()
            )
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
