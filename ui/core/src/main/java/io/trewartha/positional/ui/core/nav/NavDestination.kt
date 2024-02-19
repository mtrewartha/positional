package io.trewartha.positional.ui.core.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface NavDestination {

    val route: String

    context(NavDestination)
    fun NavGraphBuilder.composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    )

    interface MainNavDestination : NavDestination {
        val navIcon: ImageVector
        val navLabelRes: Int
    }
}
