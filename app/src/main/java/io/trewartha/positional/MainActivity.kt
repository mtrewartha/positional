package io.trewartha.positional

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.trewartha.positional.ui.core.format.DateTimeFormatter
import io.trewartha.positional.ui.design.locals.LocalDateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dateTimeFormatter: DateTimeFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalDateTimeFormatter provides dateTimeFormatter) {
                val widthSizeClass = calculateWindowSizeClass(activity = this).widthSizeClass
                MainView(
                    navHostController = rememberNavController(),
                    windowWidthSizeClass = widthSizeClass
                )
            }
        }
    }
}
