package io.trewartha.positional

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import io.trewartha.positional.utils.FirebaseCrashlyticsTree
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

        AndroidThreeTen.init(this)

        val logTree = if (BuildConfig.DEBUG) Timber.DebugTree()
        else FirebaseCrashlyticsTree(firebaseCrashlytics)
        Timber.plant(logTree)

        PreferenceManager.setDefaultValues(
            this,
            getString(R.string.settings_filename),
            Context.MODE_PRIVATE,
            R.xml.settings,
            true
        )
    }
}