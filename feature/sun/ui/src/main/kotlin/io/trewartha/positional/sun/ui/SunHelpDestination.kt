package io.trewartha.positional.sun.ui

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

public data object SunHelpDestination : NavDestination {

    override val route: String = "sun/help"

    context(navGraphBuilder: NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        navGraphBuilder.composable(
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
