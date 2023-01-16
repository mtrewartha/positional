package io.trewartha.positional

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import io.trewartha.positional.utils.log.FirebaseCrashlyticsTree
import timber.log.Timber

@HiltAndroidApp
class PositionalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        val firebaseCrashlytics = FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        }

        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = !BuildConfig.DEBUG

        val logTree = if (BuildConfig.DEBUG) Timber.DebugTree()
        else FirebaseCrashlyticsTree(firebaseCrashlytics)
        Timber.plant(logTree)
    }
}
