package io.trewartha.positional

import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.perf.FirebasePerformance

class Application : android.app.Application() {

    companion object {
        private const val TAG = "Application"
    }

    override fun onCreate() {
        super.onCreate()

        val enableFirebase = !BuildConfig.DEBUG
        Log.debug(TAG, "Firebase ${if (enableFirebase) "enabled" else "disabled"}")
        FirebaseCrash.setCrashCollectionEnabled(enableFirebase)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = enableFirebase
    }
}