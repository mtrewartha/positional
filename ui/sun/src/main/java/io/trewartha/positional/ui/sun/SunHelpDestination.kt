package io.trewartha.positional.ui.sun

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.ui.core.nav.NavDestination
import io.trewartha.positional.ui.core.nav.defaultEnterTransition
import io.trewartha.positional.ui.core.nav.defaultExitTransition
import io.trewartha.positional.ui.core.nav.defaultPopEnterTransition
import io.trewartha.positional.ui.core.nav.defaultPopExitTransition

data object SunHelpDestination : NavDestination {

    override val route = "sun/help"

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
            SunHelpView(contentPadding, onUpClick = { navController.popBackStack() })
        }
    }
}
