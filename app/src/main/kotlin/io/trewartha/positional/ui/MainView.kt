package io.trewartha.positional.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import io.trewartha.positional.ui.Screen.BottomNavigable.Compass
import io.trewartha.positional.ui.Screen.BottomNavigable.Location
import io.trewartha.positional.ui.Screen.BottomNavigable.Settings
import io.trewartha.positional.ui.Screen.BottomNavigable.Twilight
import io.trewartha.positional.ui.Screen.Help
import io.trewartha.positional.ui.compass.CompassView
import io.trewartha.positional.ui.location.LocationView
import io.trewartha.positional.ui.location.help.LocationHelpView
import io.trewartha.positional.ui.settings.SettingsView
import io.trewartha.positional.ui.twilight.TwilightView

@Composable
fun MainView(
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(Location, Compass, Twilight, Settings).forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = onClick@{
                            if (currentRoute == screen.route) return@onClick
                            navHostController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = screen.navIcon, null) },
                    )
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navHostController,
            startDestination = Location.route,
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(WindowInsets.safeContent)
        ) {
            composable(Location.route) { LocationView(navController = navHostController) }
            composable(Compass.route) { CompassView() }
            composable(Twilight.route) { TwilightView() }
            composable(Settings.route) { SettingsView() }
            composable(Help.route) { LocationHelpView(navController = navHostController) }
        }
    }
}
