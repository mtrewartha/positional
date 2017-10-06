package io.trewartha.positional

import com.google.firebase.crash.FirebaseCrash

class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }
}