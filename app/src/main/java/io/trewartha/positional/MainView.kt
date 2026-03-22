package io.trewartha.positional

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.trewartha.positional.compass.ui.CompassHelpNavKey
import io.trewartha.positional.compass.ui.CompassHelpView
import io.trewartha.positional.compass.ui.CompassNavKey
import io.trewartha.positional.compass.ui.CompassView
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.activity
import io.trewartha.positional.core.ui.nav.MainNavKey
import io.trewartha.positional.location.ui.LocationHelpNavKey
import io.trewartha.positional.location.ui.LocationHelpView
import io.trewartha.positional.location.ui.LocationNavKey
import io.trewartha.positional.location.ui.LocationPermissionRequiredContent
import io.trewartha.positional.location.ui.LocationView
import io.trewartha.positional.settings.Theme
import io.trewartha.positional.settings.ui.SettingsNavKey
import io.trewartha.positional.settings.ui.SettingsView
import io.trewartha.positional.settings.ui.SettingsViewModel
import io.trewartha.positional.sun.ui.SunHelpNavKey
import io.trewartha.positional.sun.ui.SunHelpView
import io.trewartha.positional.sun.ui.SunNavKey
import io.trewartha.positional.sun.ui.SunView
import kotlin.math.roundToInt

@Composable
public fun MainView(
    viewModel: SettingsViewModel = hiltViewModel(),
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme by remember(theme) {
        derivedStateOf {
            when (theme) {
                Theme.DEVICE -> isSystemInDarkTheme
                Theme.DARK -> true
                Theme.LIGHT -> false
                null -> false
            }
        }
    }
    val systemBarStyle =
        SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { useDarkTheme }

    LocalContext.current.activity?.enableEdgeToEdge(systemBarStyle, systemBarStyle)

    PositionalTheme(useDarkTheme = useDarkTheme) {
        val context = LocalContext.current
        val locationPermissions = remember { listOf(Manifest.permission.ACCESS_FINE_LOCATION) }
        val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
        val isCompactWidthWindow = windowWidthSizeClass == WindowWidthSizeClass.Compact
        val mainNavKeys = remember { listOf(LocationNavKey, CompassNavKey, SunNavKey, SettingsNavKey) }
        val snackbarHostState = remember { SnackbarHostState() }

        if (locationPermissionsState.allPermissionsGranted) {
            val locationBackStack = rememberNavBackStack(LocationNavKey)
            val compassBackStack = rememberNavBackStack(CompassNavKey)
            val sunBackStack = rememberNavBackStack(SunNavKey)
            val settingsBackStack = rememberNavBackStack(SettingsNavKey)
            var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }
            val (activeTab, activeBackStack) = when (activeTabIndex) {
                1 -> CompassNavKey to compassBackStack
                2 -> SunNavKey to sunBackStack
                3 -> SettingsNavKey to settingsBackStack
                else -> LocationNavKey to locationBackStack
            }
            val entryDecorators = listOf<NavEntryDecorator<NavKey>>(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            )
            Scaffold(
                bottomBar = bottomBar@{
                    if (!isCompactWidthWindow) return@bottomBar
                    MainNavigationBar(mainNavKeys, activeTab) onTabSelected@{ index ->
                        activeTabIndex = index
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            ) { contentPadding ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isCompactWidthWindow) {
                        MainNavigationRail(
                            mainNavKeys,
                            activeTab,
                            modifier = Modifier.fillMaxHeight()
                        ) { index -> activeTabIndex = index }
                    }
                    NavDisplay(
                        backStack = activeBackStack,
                        onBack = { activeBackStack.removeLastOrNull() },
                        entryDecorators = entryDecorators,
                        entryProvider = entryProvider {
                            locationEntries(
                                context,
                                contentPadding,
                                snackbarHostState,
                                locationBackStack
                            )
                            compassEntries(contentPadding, compassBackStack)
                            sunEntries(contentPadding, sunBackStack)
                            settingsEntries(context, contentPadding)
                        }
                    )
                }
            }
        } else {
            LocationPermissionRequiredContent(
                locationPermissionsState,
                onSettingsClick = { viewSystemSettings(context) },
                Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MainNavigationBar(
    mainNavKeys: List<MainNavKey>,
    activeMainNavKey: MainNavKey,
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit,
) {
    NavigationBar(modifier) {
        mainNavKeys.forEachIndexed { index, mainNavKey ->
            NavigationBarItem(
                selected = activeMainNavKey == mainNavKey,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = mainNavKey.navIcon, null) },
                label = { Text(stringResource(mainNavKey.navLabelRes)) }
            )
        }
    }
}

@Composable
private fun MainNavigationRail(
    mainNavKeys: List<MainNavKey>,
    activeMainNavKey: MainNavKey,
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit,
) {
    NavigationRail(modifier) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            mainNavKeys.forEachIndexed { index, mainNavKey ->
                NavigationRailItem(
                    selected = activeMainNavKey == mainNavKey,
                    onClick = { onItemSelected(index) },
                    icon = { Icon(imageVector = mainNavKey.navIcon, null) },
                    label = { Text(stringResource(mainNavKey.navLabelRes)) },
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

private fun shareCoordinates(context: Context, formattedCoordinates: String) {
    val shareCoordinatesIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, formattedCoordinates)
        type = "text/plain"
    }
    context.startActivity(shareCoordinatesIntent)
}

private fun viewLicense(context: Context) {
    val viewLicenseIntent = Intent(Intent.ACTION_VIEW, LICENSE_URI.toUri())
    context.startActivity(viewLicenseIntent)
}

private fun viewPrivacyPolicy(context: Context) {
    val viewPrivacyPolicyIntent = Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URI.toUri())
    context.startActivity(viewPrivacyPolicyIntent)
}

private fun viewSystemSettings(context: Context) {
    val viewSettingsClick = Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(viewSettingsClick)
}

private fun EntryProviderScope<NavKey>.compassEntries(
    contentPadding: PaddingValues,
    compassBackStack: NavBackStack<NavKey>
) {
    entry<CompassNavKey> {
        CompassView(
            contentPadding = contentPadding,
            onHelpClick = { compassBackStack.add(CompassHelpNavKey) },
        )
    }
    entry<CompassHelpNavKey>(metadata = helpScreenTransitions) {
        CompassHelpView(
            contentPadding,
            onUpClick = { compassBackStack.removeLastOrNull() }
        )
    }
}

private fun EntryProviderScope<NavKey>.locationEntries(
    context: Context,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    locationBackStack: NavBackStack<NavKey>
) {
    entry<LocationNavKey> {
        LocationView(
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onShareClick = { formattedCoordinates ->
                shareCoordinates(context, formattedCoordinates)
            },
            onHelpClick = { locationBackStack.add(LocationHelpNavKey) }
        )
    }
    entry<LocationHelpNavKey>(metadata = helpScreenTransitions) {
        LocationHelpView(
            contentPadding,
            onUpClick = { locationBackStack.removeLastOrNull() }
        )
    }
}

private fun EntryProviderScope<NavKey>.settingsEntries(
    context: Context,
    contentPadding: PaddingValues
) {
    entry<SettingsNavKey> {
        SettingsView(
            contentPadding = contentPadding,
            onLicenseClick = { viewLicense(context) },
            onPrivacyPolicyClick = { viewPrivacyPolicy(context) },
        )
    }
}

private fun EntryProviderScope<NavKey>.sunEntries(
    contentPadding: PaddingValues,
    sunBackStack: NavBackStack<NavKey>
) {
    entry<SunNavKey> {
        SunView(
            contentPadding = contentPadding,
            onHelpClick = { sunBackStack.add(SunHelpNavKey) },
        )
    }
    entry<SunHelpNavKey>(metadata = helpScreenTransitions) {
        SunHelpView(
            contentPadding,
            onUpClick = { sunBackStack.removeLastOrNull() }
        )
    }
}

private const val LICENSE_URI = "https://github.com/mtrewartha/positional/blob/main/LICENSE"
private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/main/PRIVACY.md"
private const val OFFSET_FACTOR = 0.4f

// Metadata map for help screens: slide in from right on enter, slide out to right on pop.
private val helpScreenTransitions: Map<String, Any> =
    NavDisplay.transitionSpec {
        (fadeIn() + slideIntoContainer(
            Start,
            initialOffset = { (it * OFFSET_FACTOR).roundToInt() }))
            .togetherWith(
                fadeOut() + slideOutOfContainer(
                    Start,
                    targetOffset = { (it * OFFSET_FACTOR).roundToInt() })
            )
    } + NavDisplay.popTransitionSpec {
        (fadeIn() + slideIntoContainer(End, initialOffset = { (it * OFFSET_FACTOR).roundToInt() }))
            .togetherWith(
                fadeOut() + slideOutOfContainer(
                    End,
                    targetOffset = { (it * OFFSET_FACTOR).roundToInt() })
            )
    }