package io.trewartha.positional

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroApplication
import timber.log.Timber

public class PositionalApplication : Application(), MetroApplication {

    override val appComponentProviders: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree())
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
