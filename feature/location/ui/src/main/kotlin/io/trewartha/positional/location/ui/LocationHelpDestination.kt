package io.trewartha.positional.location.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.core.ui.nav.NavDestination
import io.trewartha.positional.core.ui.nav.defaultEnterTransition
import io.trewartha.positional.core.ui.nav.defaultExitTransition
import io.trewartha.positional.core.ui.nav.defaultPopEnterTransition
import io.trewartha.positional.core.ui.nav.defaultPopExitTransition

data object LocationHelpDestination : NavDestination {

    override val route = "location/help"

    context(NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        composable(
            route,
            enterTransition = defaultEnterTransition(),
            exitTransition = defaultExitTransition(),
            popEnterTransition = defaultPopEnterTransition(),
            popExitTransition = defaultPopExitTransition()
        ) {
            LocationHelpView(contentPadding, onUpClick = { navController.popBackStack() })
        }
    }
}
