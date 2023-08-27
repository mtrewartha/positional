@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package io.trewartha.positional.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.compass.compassView
import io.trewartha.positional.ui.location.locationView
import io.trewartha.positional.ui.settings.settingsView
import io.trewartha.positional.ui.solunar.solunarView

@Composable
fun MainView(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val theme by viewModel.theme.collectAsState(initial = Theme.DEVICE)
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
    PositionalTheme(useDarkTheme = useDarkTheme) {
        val isCompactScreen = windowWidthSizeClass == WindowWidthSizeClass.Compact
        val mainNavDestinations = setOf(Location, Compass, /*Solunar,*/ Settings)
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = bottomBar@{
                if (!isCompactScreen) return@bottomBar
                MainNavigationBar(navHostController, mainNavDestinations)
            },
        ) { scaffoldPadding ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (windowWidthSizeClass != WindowWidthSizeClass.Compact) {
                    MainNavigationRail(
                        navHostController,
                        mainNavDestinations,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                }
                val context = LocalContext.current
                NavHost(
                    navHostController,
                    startDestination = Location.route,
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .consumeWindowInsets(scaffoldPadding)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                ) {
                    compassView()
                    locationView(onAndroidSettingsClick = { navigateToSettings(context) })
                    settingsView(onPrivacyPolicyClick = { navigateToPrivacyPolicy(context) })
                    solunarView()
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
                onClick = onClick@{
                    if (currentRoute == mainNavDestination.route) return@onClick
                    navHostController.navigate(mainNavDestination.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
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
                    onClick = onClick@{
                        if (currentRoute == mainNavDestination.route) return@onClick
                        navHostController.navigate(mainNavDestination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
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

private fun navigateToPrivacyPolicy(context: Context) {
    val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
    ContextCompat.startActivity(context, privacyPolicyIntent, null)
}

private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/master/PRIVACY.md"
