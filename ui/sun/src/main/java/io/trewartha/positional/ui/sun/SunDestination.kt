package io.trewartha.positional.ui.sun

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.ui.core.nav.NavDestination
import io.trewartha.positional.ui.core.nav.bottomNavEnterTransition
import io.trewartha.positional.ui.core.nav.bottomNavExitTransition
import io.trewartha.positional.ui.core.nav.bottomNavPopEnterTransition
import io.trewartha.positional.ui.core.nav.bottomNavPopExitTransition

data object SunDestination : NavDestination.MainNavDestination {

    override val route = "sun"
    override val navIcon = Icons.Rounded.WbTwilight
    override val navLabelRes = R.string.ui_sun_title

    context(NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        composable(
            route,
            enterTransition = bottomNavEnterTransition(),
            exitTransition = bottomNavExitTransition(SunHelpDestination.route),
            popEnterTransition = bottomNavPopEnterTransition(SunHelpDestination.route),
            popExitTransition = bottomNavPopExitTransition()
        ) {
            val viewModel: SunViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SunView(
                state = state,
                contentPadding = contentPadding,
                onSelectedDateDecrement = viewModel::onSelectedDateDecrement,
                onDateSelection = viewModel::onSelectedDateChange,
                onSelectedDateIncrement = viewModel::onSelectedDateIncrement,
                onJumpToTodayClick = viewModel::onSelectedDateChangedToToday,
                onHelpClick = { navController.navigate(SunHelpDestination.route) }
            )
        }
    }
}
