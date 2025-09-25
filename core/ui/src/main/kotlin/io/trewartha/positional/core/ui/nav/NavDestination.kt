package io.trewartha.positional.core.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

public interface NavDestination {

    public val route: String

    context(navGraphBuilder: NavGraphBuilder)
    public fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    )

    public interface MainNavDestination : NavDestination {
        public val navIcon: ImageVector
        public val navLabelRes: Int
    }
}
