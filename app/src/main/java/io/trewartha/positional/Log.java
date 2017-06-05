package io.trewartha.positional;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.crash.FirebaseCrash;

public class Log {

    private static final int LOG_LEVEL_DEBUG = 1;
    private static final int LOG_LEVEL_INFO = 2;
    private static final int LOG_LEVEL_WARN = 3;
    private static final int LOG_LEVEL_ERROR = 4;

    public static void debug(@NonNull String tag, @Nullable String message) {
        log(LOG_LEVEL_DEBUG, tag, message, null);
    }

    public static void debug(@NonNull String tag, @Nullable String message, @NonNull Throwable throwable) {
        log(LOG_LEVEL_DEBUG, tag, message, throwable);
    }

    public static void info(@NonNull String tag, @Nullable String message) {
        log(LOG_LEVEL_INFO, tag, message, null);
    }

    public static void info(@NonNull String tag, @Nullable String message, @NonNull Throwable throwable) {
        log(LOG_LEVEL_INFO, tag, message, throwable);
    }

    public static void warn(@NonNull String tag, @Nullable String message) {
        log(LOG_LEVEL_WARN, tag, message, null);
    }

    public static void warn(@NonNull String tag, @Nullable String message, @NonNull Throwable throwable) {
        log(LOG_LEVEL_WARN, tag, message, throwable);
    }

    public static void error(@NonNull String tag, @Nullable String message) {
        log(LOG_LEVEL_ERROR, tag, message, null);
    }

    public static void error(@NonNull String tag, @Nullable String message, @NonNull Throwable throwable) {
        log(LOG_LEVEL_ERROR, tag, message, throwable);
    }

    private static void log(int logLevel, @NonNull String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (BuildConfig.DEBUG) {
            sendToLogcat(logLevel, tag, message, throwable);
        } else {
            sendToFirebase(tag, message, throwable);
        }
    }

    private static void sendToFirebase(@NonNull String tag, @Nullable String message, @Nullable Throwable throwable) {
        FirebaseCrash.log(tag + " - " + message);
        if (throwable != null) {
            FirebaseCrash.report(throwable);
        }
    }

    private static void sendToLogcat(int logLevel, @NonNull String tag, @Nullable String message, @Nullable Throwable throwable) {
        switch (logLevel) {
            case LOG_LEVEL_DEBUG:
                if (throwable == null) {
                    android.util.Log.d(tag, message);
                } else {
                    android.util.Log.d(tag, message, throwable);
                }
                break;
            case LOG_LEVEL_INFO:
                if (throwable == null) {
                    android.util.Log.i(tag, message);
                } else {
                    android.util.Log.i(tag, message, throwable);
                }
                break;
            case LOG_LEVEL_WARN:
                if (throwable == null) {
                    android.util.Log.w(tag, message);
                } else {
                    android.util.Log.w(tag, message, throwable);
                }
                break;
            case LOG_LEVEL_ERROR:
                if (throwable == null) {
                    android.util.Log.e(tag, message);
                } else {
                    android.util.Log.e(tag, message, throwable);
                }
                break;
        }
    }
}
