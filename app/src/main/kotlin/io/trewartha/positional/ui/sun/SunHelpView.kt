package io.trewartha.positional.ui.sun

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.components.HelpView
import io.trewartha.positional.ui.defaultEnterTransition
import io.trewartha.positional.ui.defaultExitTransition
import io.trewartha.positional.ui.defaultPopEnterTransition
import io.trewartha.positional.ui.defaultPopExitTransition

fun NavGraphBuilder.sunHelpView(
    navController: NavController,
    contentPadding: PaddingValues
) {
    composable(
        NavDestination.SunHelp.route,
        enterTransition = defaultEnterTransition(),
        exitTransition = defaultExitTransition(),
        popEnterTransition = defaultPopEnterTransition(),
        popExitTransition = defaultPopExitTransition()
    ) {
        SunHelpView(contentPadding, onUpClick = { navController.popBackStack() })
    }
}

@Composable
fun SunHelpView(
    contentPadding: PaddingValues,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HelpView(
        title = { Text(text = stringResource(id = R.string.sun_help_title)) },
        markdownRes = R.raw.sun_help,
        contentPadding = contentPadding,
        onUpClick = onUpClick,
        modifier = modifier
    )
}
