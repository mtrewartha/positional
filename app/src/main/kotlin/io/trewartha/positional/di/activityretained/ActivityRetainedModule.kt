package io.trewartha.positional.di.activityretained

import android.content.Context
import android.view.WindowManager
import androidx.core.os.LocaleListCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter
import kotlinx.coroutines.Dispatchers
import java.util.Locale
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    fun coroutineContext(): CoroutineContext = Dispatchers.Default

    @Provides
    fun dateTimeFormatter(
        systemDateTimeFormatter: SystemDateTimeFormatter
    ): DateTimeFormatter = systemDateTimeFormatter

    @Provides
    fun locale(): Locale = checkNotNull(LocaleListCompat.getDefault()[0])

    @Provides
    fun windowManager(
        @ApplicationContext context: Context
    ): WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}
