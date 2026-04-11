package io.trewartha.positional

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
public class MainActivity(
    private val dateTimeFormatter: DateTimeFormatter,
    private val viewModelFactory: MetroViewModelFactory
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalDateTimeFormatter provides dateTimeFormatter,
                LocalMetroViewModelFactory provides viewModelFactory
            ) {
                val widthSizeClass = calculateWindowSizeClass(activity = this).widthSizeClass
                MainView(windowWidthSizeClass = widthSizeClass)
            }
        }
    }
}
