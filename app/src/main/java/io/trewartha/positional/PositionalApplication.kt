package io.trewartha.positional

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.FirebasePerformance
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.trewartha.positional.utils.CrashlyticsTree
import timber.log.Timber


class PositionalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = !BuildConfig.DEBUG
        AndroidThreeTen.init(this)
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree())

        if (!BuildConfig.DEBUG) {
            Fabric.with(Fabric.Builder(this)
                    .kits(Crashlytics())
                    .debuggable(true)
                    .build())
        }
    }
}