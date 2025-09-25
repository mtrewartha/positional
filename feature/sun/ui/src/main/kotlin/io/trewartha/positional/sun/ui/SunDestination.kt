package io.trewartha.positional.sun.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.core.ui.nav.NavDestination
import io.trewartha.positional.core.ui.nav.bottomNavEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavExitTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopExitTransition

public data object SunDestination : NavDestination.MainNavDestination {

    override val route: String = "sun"
    override val navIcon: ImageVector = Icons.Rounded.WbTwilight
    override val navLabelRes: Int = R.string.feature_sun_ui_title

    context(navGraphBuilder: NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        navGraphBuilder.composable(
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
