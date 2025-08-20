package io.trewartha.positional.location.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.core.ui.locals.LocalLocale
import io.trewartha.positional.core.ui.nav.NavDestination
import io.trewartha.positional.core.ui.nav.bottomNavEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavExitTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopEnterTransition
import io.trewartha.positional.core.ui.nav.bottomNavPopExitTransition
import io.trewartha.positional.location.ui.format.DecimalDegreesFormatter
import io.trewartha.positional.location.ui.format.DegreesDecimalMinutesFormatter
import io.trewartha.positional.location.ui.format.DegreesMinutesSecondsFormatter
import io.trewartha.positional.location.ui.format.MgrsFormatter
import io.trewartha.positional.location.ui.format.UtmFormatter
import io.trewartha.positional.location.ui.locals.LocalCoordinatesFormatter
import io.trewartha.positional.settings.CoordinatesFormat

data object LocationDestination : NavDestination.MainNavDestination {

    override val route = "location"
    override val navIcon = Icons.Rounded.MyLocation
    override val navLabelRes = R.string.feature_location_ui_title

    context(NavGraphBuilder)
    override fun composable(
        navController: NavController,
        snackbarHostState: SnackbarHostState,
        contentPadding: PaddingValues
    ) {
        composable(
            route,
            enterTransition = bottomNavEnterTransition(),
            exitTransition = bottomNavExitTransition(LocationHelpDestination.route),
            popEnterTransition = bottomNavPopEnterTransition(LocationHelpDestination.route),
            popExitTransition = bottomNavPopExitTransition()
        ) {
            val viewModel: LocationViewModel = hiltViewModel()
            val locationState by viewModel.location.collectAsStateWithLifecycle()
            val settingsState by viewModel.settings.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val locale = LocalLocale.current
            CompositionLocalProvider(
                LocalCoordinatesFormatter provides
                        when (settingsState.dataOrNull?.coordinatesFormat) {
                            CoordinatesFormat.DD, null -> DecimalDegreesFormatter(context, locale)
                            CoordinatesFormat.DDM -> DegreesDecimalMinutesFormatter(context, locale)
                            CoordinatesFormat.DMS -> DegreesMinutesSecondsFormatter(context, locale)
                            CoordinatesFormat.MGRS -> MgrsFormatter()
                            CoordinatesFormat.UTM -> UtmFormatter(context, locale)
                        }
            ) {
                val coordinatesFormatter = LocalCoordinatesFormatter.current
                LocationView(
                    locationState,
                    settingsState,
                    contentPadding,
                    snackbarHostState,
                    onShareClick = click@{ coordinates ->
                        val formattedCoordinates =
                            coordinates?.let { coordinatesFormatter.formatForCopy(it) }
                                ?: return@click
                        shareCoordinates(context, formattedCoordinates)
                    },
                    onHelpClick = { navController.navigate(LocationHelpDestination.route) }
                )
            }
        }
    }
}

private fun shareCoordinates(context: Context, formattedCoordinates: String) {
    ContextCompat.startActivity(
        context,
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, formattedCoordinates)
            type = "text/plain"
        },
        null
    )
}
