package io.trewartha.positional.utils.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class FirebaseCrashlyticsTree(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        firebaseCrashlytics.log(message)
        t?.let { firebaseCrashlytics.recordException(it) }
    }
}