package io.trewartha.positional

import android.app.Application
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.perf.FirebasePerformance
import com.jakewharton.threetenabp.AndroidThreeTen
import io.trewartha.positional.common.Log

class PositionalApplication : Application() {

    companion object {
        private const val TAG = "Positional"
    }

    override fun onCreate() {
        super.onCreate()

        val enableFirebase = !BuildConfig.DEBUG
        Log.debug(TAG, "Firebase ${if (enableFirebase) "enabled" else "disabled"}")
        FirebaseCrash.setCrashCollectionEnabled(enableFirebase)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = enableFirebase

        AndroidThreeTen.init(this)
    }
}