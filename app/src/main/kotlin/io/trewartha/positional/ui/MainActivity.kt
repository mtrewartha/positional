package io.trewartha.positional.ui

import android.os.Bundle
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
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.trewartha.positional.BuildConfig
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
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
                    MainView(navHostController = rememberNavController())
                }
            }
        }
    }
}
