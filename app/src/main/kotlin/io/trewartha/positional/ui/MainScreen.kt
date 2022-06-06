package io.trewartha.positional.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.navigationBarsPadding
import io.trewartha.positional.ui.Screen.BottomNavigable.Compass
import io.trewartha.positional.ui.Screen.BottomNavigable.Location
import io.trewartha.positional.ui.Screen.BottomNavigable.Settings
import io.trewartha.positional.ui.Screen.BottomNavigable.Sun
import io.trewartha.positional.ui.Screen.Help
import io.trewartha.positional.ui.location.LocationScreen
import io.trewartha.positional.ui.location.help.LocationHelpScreen

@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) { // TODO: Handle window insets in the new way (see Accompanist page for why this isn't the correct way anymore)
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(
                    Location,
                    Compass,
                    Sun,
                    Settings
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(screen.navIconRes), null) },
                        label = { Text(stringResource(screen.navLabelRes)) },
                        selected = currentRoute == screen.route,
                        onClick = {
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
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navHostController,
            startDestination = Location.route,
            modifier = Modifier
                .padding(bottom = contentPadding.calculateBottomPadding())
        ) {
            composable(Location.route) { LocationScreen(navController = navHostController) }
            composable(Help.route) { LocationHelpScreen(navController = navHostController) }
        }
    }
}
