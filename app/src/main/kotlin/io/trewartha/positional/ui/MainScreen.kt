package io.trewartha.positional.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.trewartha.positional.ui.location.LocationScreen
import io.trewartha.positional.ui.location.LocationViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = if (isSystemInDarkTheme())
                    MaterialTheme.colors.primarySurface
                else
                    LightColors.surface,
                elevation = 4.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(
                    Screen.BottomNavigable.Location,
                    Screen.BottomNavigable.Compass,
                    Screen.BottomNavigable.Sun,
                    Screen.BottomNavigable.Settings
                ).forEach { screen ->
                    BottomNavigationItem(
                        alwaysShowLabel = false,
                        icon = { Icon(painterResource(screen.navIconRes), null) },
                        label = { Text(stringResource(screen.navLabelRes)) },
                        selectedContentColor = if (isSystemInDarkTheme())
                            LocalContentColor.current
                        else
                            MaterialTheme.colors.primary,
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) {
        NavHost(
            navController,
            startDestination = Screen.BottomNavigable.Location.route,
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {
            composable(Screen.BottomNavigable.Location.route) {
                val viewModel = hiltViewModel<LocationViewModel>()
                LocationScreen(
                    viewModel.coordinatesStateFlow.collectAsState().value,
                    viewModel.screenLockEnabledFlow.collectAsState().value,
                    viewModel.locationStatsStateFlow.collectAsState().value,
                    viewModel::onCopyClick,
                    viewModel::onHelpClick,
                    viewModel::onScreenLockCheckedChange,
                    viewModel::onShareClick,
                )
            }
            composable(Screen.BottomNavigable.Compass.route) {
                val viewModel = hiltViewModel<LocationViewModel>()
                LocationScreen(
                    viewModel.coordinatesStateFlow.collectAsState().value,
                    viewModel.screenLockEnabledFlow.collectAsState().value,
                    viewModel.locationStatsStateFlow.collectAsState().value,
                    viewModel::onCopyClick,
                    viewModel::onHelpClick,
                    viewModel::onScreenLockCheckedChange,
                    viewModel::onShareClick,
                )
            }
            composable(Screen.BottomNavigable.Sun.route) {
                val viewModel = hiltViewModel<LocationViewModel>()
                LocationScreen(
                    viewModel.coordinatesStateFlow.collectAsState().value,
                    viewModel.screenLockEnabledFlow.collectAsState().value,
                    viewModel.locationStatsStateFlow.collectAsState().value,
                    viewModel::onCopyClick,
                    viewModel::onHelpClick,
                    viewModel::onScreenLockCheckedChange,
                    viewModel::onShareClick,
                )
            }
            composable(Screen.BottomNavigable.Settings.route) {
                val viewModel = hiltViewModel<LocationViewModel>()
                LocationScreen(
                    viewModel.coordinatesStateFlow.collectAsState().value,
                    viewModel.screenLockEnabledFlow.collectAsState().value,
                    viewModel.locationStatsStateFlow.collectAsState().value,
                    viewModel::onCopyClick,
                    viewModel::onHelpClick,
                    viewModel::onScreenLockCheckedChange,
                    viewModel::onShareClick,
                )
            }
        }
    }
}