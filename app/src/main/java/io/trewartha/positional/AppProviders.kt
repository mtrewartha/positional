package io.trewartha.positional

import android.app.Application
import android.view.WindowManager
import androidx.core.os.LocaleListCompat
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers

@ContributesTo(AppScope::class)
public interface AppProviders {

    @Provides
    fun clock(): Clock = Clock.System

    @Provides
    fun coroutineContext(): CoroutineContext = Dispatchers.Default

    @Provides
    fun locale(): Locale = checkNotNull(LocaleListCompat.getDefault()[0])

    @Provides
    fun windowManager(application: Application): WindowManager =
        application.getSystemService(WindowManager::class.java)
}
