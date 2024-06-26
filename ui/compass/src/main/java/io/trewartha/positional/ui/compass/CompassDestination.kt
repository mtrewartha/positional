package io.trewartha.positional.ui.compass

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
import io.trewartha.positional.ui.design.locals.LocalVibrator

data object CompassDestination : NavDestination.MainNavDestination {

    override val route = "compass"
    override val navIcon = Icons.Rounded.Explore
    override val navLabelRes = R.string.ui_compass_title

    context(NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        composable(
            route,
            enterTransition = bottomNavEnterTransition(),
            exitTransition = bottomNavExitTransition(CompassHelpDestination.route),
            popEnterTransition = bottomNavPopEnterTransition(CompassHelpDestination.route),
            popExitTransition = bottomNavPopExitTransition()
        ) {
            val viewModel: CompassViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            CompositionLocalProvider(
                LocalVibrator provides
                        @Suppress("DEPRECATION") // It matches our needs and goes back pre API 21
                        LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            ) {
                CompassView(
                    state = state,
                    contentPadding = contentPadding,
                    onHelpClick = { navController.navigate(CompassHelpDestination.route) })
            }
        }
    }
}
