package io.trewartha.positional.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import io.trewartha.positional.R
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dateTimeFormatter: DateTimeFormatter

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val theme by mainViewModel.theme.collectAsState(initial = Theme.DEVICE)
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val useDarkTheme by remember {
                derivedStateOf {
                    when (theme) {
                        Theme.DEVICE -> isSystemInDarkTheme
                        Theme.DARK -> true
                        Theme.LIGHT -> false
                    }
                }
            }
            PositionalTheme(useDarkTheme = useDarkTheme) {
                CompositionLocalProvider(LocalDateTimeFormatter provides dateTimeFormatter) {
                    MainView(
                        navHostController = rememberAnimatedNavController(),
                        onNavigateToMap = ::navigateToMap,
                        onNavigateToPrivacyPolicy = ::navigateToPrivacyPolicy,
                        onNavigateToSettings = ::navigateToSettings
                    )
                }
            }
        }
    }

    private fun navigateToMap(
        latitude: Double,
        longitude: Double,
        localDateTime: LocalDateTime
    ) {
        val formattedDateTime = dateTimeFormatter.formatDateTime(localDateTime)
        val label = getString(R.string.location_launch_label, formattedDateTime)
        val geoUri = Uri.Builder()
            .scheme("geo")
            .path("$latitude,$longitude")
            .appendQueryParameter("q", "$latitude,$longitude($label)")
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, geoUri))
    }

    private fun navigateToPrivacyPolicy() {
        val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
        startActivity(privacyPolicyIntent)
    }

    private fun navigateToSettings() {
        val settingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        startActivity(settingsIntent)
    }
}

private const val PRIVACY_POLICY_URI =
    "https://github.com/mtrewartha/positional/blob/master/PRIVACY.md"
