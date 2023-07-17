@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package io.trewartha.positional.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import io.trewartha.positional.ui.NavDestination.Compass
import io.trewartha.positional.ui.NavDestination.Location
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.compass.compassView
import io.trewartha.positional.ui.location.locationView
import io.trewartha.positional.ui.settings.settingsView
import io.trewartha.positional.ui.solunar.solunarView

@Composable
fun MainView(navHostController: NavHostController) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                listOf(Location, Compass, /*Solunar,*/ Settings).forEach { screen ->
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
    ) { scaffoldPadding ->
        val context = LocalContext.current
        NavHost(
            navHostController,
            startDestination = Location.route,
            modifier = Modifier
                .padding(scaffoldPadding)
                .consumeWindowInsets(scaffoldPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            compassView()
            locationView(onAndroidSettingsClick = { navigateToSettings(context) })
            settingsView(onPrivacyPolicyClick = { navigateToPrivacyPolicy(context) })
            solunarView()
        }
    }
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
