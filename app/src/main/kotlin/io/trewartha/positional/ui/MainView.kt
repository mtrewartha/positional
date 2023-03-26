package io.trewartha.positional.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.NavDestination.Solunar
import io.trewartha.positional.ui.compass.compassView
import io.trewartha.positional.ui.compass.info.compassInfoView
import io.trewartha.positional.ui.compass.info.navigateToCompassInfo
import io.trewartha.positional.ui.location.info.locationInfoView
import io.trewartha.positional.ui.location.info.navigateToLocationInfo
import io.trewartha.positional.ui.location.locationView
import io.trewartha.positional.ui.settings.settingsView
import io.trewartha.positional.ui.solunar.info.navigateToSolunarInfo
import io.trewartha.positional.ui.solunar.info.solunarInfoView
import io.trewartha.positional.ui.solunar.solunarView
import kotlinx.datetime.LocalDateTime

@Composable
fun MainView(
    navHostController: NavHostController,
    onNavigateToMap: (Double, Double, LocalDateTime) -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(Location, Compass, Solunar, Settings).forEach { screen ->
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
        AnimatedNavHost(
            navHostController,
            startDestination = Location.route,
            enterTransition = { slideInHorizontally { it / 2 } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it / 2 } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it / 2 } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it / 2 } + fadeOut() },
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(WindowInsets.safeContent)
        ) {
            compassView(onNavigateToInfo = { navHostController.navigateToCompassInfo() })
            compassInfoView(onNavigateUp = { navHostController.popBackStack() })
            locationView(
                onNavigateToInfo = { navHostController.navigateToLocationInfo() },
                onNavigateToMap = onNavigateToMap,
                onNavigateToSettings = onNavigateToSettings,
            )
            locationInfoView(onNavigateUp = { navHostController.popBackStack() })
            settingsView(onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy)
            solunarView(onNavigateToInfo = { navHostController.navigateToSolunarInfo() })
            solunarInfoView(onNavigateUp = { navHostController.popBackStack() })
        }
    }
}
