@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package io.trewartha.positional.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.trewartha.positional.R
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.NavDestination.Solunar
import io.trewartha.positional.ui.compass.compassView
import io.trewartha.positional.ui.compass.info.compassInfoView
import io.trewartha.positional.ui.compass.info.navigateToCompassInfo
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.location.Coordinates
import io.trewartha.positional.ui.location.info.locationInfoView
import io.trewartha.positional.ui.location.info.navigateToLocationInfo
import io.trewartha.positional.ui.location.locationView
import io.trewartha.positional.ui.settings.settingsView
import io.trewartha.positional.ui.solunar.info.navigateToSolunarInfo
import io.trewartha.positional.ui.solunar.info.solunarInfoView
import io.trewartha.positional.ui.solunar.solunarView
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MainView(navHostController: NavHostController) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(Location, Compass, Solunar, Settings).forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(screen.route) ?: false,
                        onClick = onClick@{
                            if (currentRoute == screen.route) return@onClick
                            navHostController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = screen.navIcon, null) },
                    )
                }
            }
        }
    ) { contentPadding ->
        val context = LocalContext.current
        val dateTimeFormatter = LocalDateTimeFormatter.current
        NavHost(
            navHostController,
            startDestination = Location.route,
            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())
        ) {
            compassView(onNavigateToInfo = { navHostController.navigateToCompassInfo() })
            compassInfoView(onNavigateUp = { navHostController.popBackStack() })
            locationView(
                onAndroidSettingsClick = { navigateToSettings(context) },
                onInfoClick = { navHostController.navigateToLocationInfo() },
                onMapClick = { coordinates, timestamp ->
                    navigateToMap(context, dateTimeFormatter, coordinates, timestamp)
                }
            )
            locationInfoView(onNavigateUp = { navHostController.popBackStack() })
            settingsView(onPrivacyPolicyClick = { navigateToPrivacyPolicy(context) })
            solunarView(onNavigateToInfo = { navHostController.navigateToSolunarInfo() })
            solunarInfoView(onNavigateUp = { navHostController.popBackStack() })
        }
    }
}

private fun navigateToMap(
    context: Context,
    dateTimeFormatter: DateTimeFormatter,
    coordinates: Coordinates?,
    timestamp: Instant?
) {
    if (coordinates == null || timestamp == null) return
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDateTime = dateTimeFormatter.formatDateTime(localDateTime)
    val label = context.getString(R.string.location_launch_label, formattedDateTime)
    val geoUri = with(coordinates) {
        Uri.Builder()
            .scheme("geo")
            .path("$latitude,$longitude")
            .appendQueryParameter("q", "$latitude,$longitude($label)")
            .build()
    }
    context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
}

private fun navigateToSettings(context: Context) {
    val settingsIntent = Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    ContextCompat.startActivity(context, settingsIntent, null)
}

private fun navigateToPrivacyPolicy(context: Context) {
    val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
    ContextCompat.startActivity(context, privacyPolicyIntent, null)
}

private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/master/PRIVACY.md"
