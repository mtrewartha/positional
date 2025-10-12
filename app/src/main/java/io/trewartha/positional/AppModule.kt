package io.trewartha.positional

import android.content.Context
import android.view.WindowManager
import androidx.core.os.LocaleListCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    fun clock(): Clock = Clock.System

    @Provides
    fun coroutineContext(): CoroutineContext = Dispatchers.Default

    @Provides
    fun locale(): Locale = checkNotNull(LocaleListCompat.getDefault()[0])

    @Provides
    fun windowManager(
        @ApplicationContext context: Context
    ): WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}
