package io.trewartha.positional.common

import com.google.firebase.crash.FirebaseCrash
import io.trewartha.positional.BuildConfig

object Log {

    private const val LEVEL_DEBUG = 1
    private const val LEVEL_INFO = 2
    private const val LEVEL_WARN = 3
    private const val LEVEL_ERROR = 4

    fun debug(tag: String, message: String?) {
        log(LEVEL_DEBUG, tag, message, null)
    }

    fun debug(tag: String, message: String?, throwable: Throwable) {
        log(LEVEL_DEBUG, tag, message, throwable)
    }

    fun info(tag: String, message: String?) {
        log(LEVEL_INFO, tag, message, null)
    }

    fun info(tag: String, message: String?, throwable: Throwable) {
        log(LEVEL_INFO, tag, message, throwable)
    }

    fun warn(tag: String, message: String?) {
        log(LEVEL_WARN, tag, message, null)
    }

    fun warn(tag: String, message: String?, throwable: Throwable) {
        log(LEVEL_WARN, tag, message, throwable)
    }

    fun error(tag: String, message: String?) {
        log(LEVEL_ERROR, tag, message, null)
    }

    fun error(tag: String, message: String?, throwable: Throwable) {
        log(LEVEL_ERROR, tag, message, throwable)
    }

    private fun log(logLevel: Int, tag: String, message: String?, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            sendToLogcat(logLevel, tag, message, throwable)
        } else {
            sendToFirebase(tag, message, throwable)
        }
    }

    private fun sendToFirebase(tag: String, message: String?, throwable: Throwable?) {
        FirebaseCrash.log(tag + " - " + message)
        if (throwable != null) {
            FirebaseCrash.report(throwable)
        }
    }

    private fun sendToLogcat(logLevel: Int, tag: String, message: String?, throwable: Throwable?) {
        when (logLevel) {
            LEVEL_DEBUG -> if (throwable == null) {
                android.util.Log.d(tag, message)
            } else {
                android.util.Log.d(tag, message, throwable)
            }
            LEVEL_INFO -> if (throwable == null) {
                android.util.Log.i(tag, message)
            } else {
                android.util.Log.i(tag, message, throwable)
            }
            LEVEL_WARN -> if (throwable == null) {
                android.util.Log.w(tag, message)
            } else {
                android.util.Log.w(tag, message, throwable)
            }
            LEVEL_ERROR -> if (throwable == null) {
                android.util.Log.e(tag, message)
            } else {
                android.util.Log.e(tag, message, throwable)
            }
        }
    }
}
