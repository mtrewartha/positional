package io.trewartha.positional

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber

internal class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        t?.let { Firebase.crashlytics.recordException(it) }
    }
}
