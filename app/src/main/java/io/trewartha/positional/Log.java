package io.trewartha.positional;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.crash.FirebaseCrash;

public class Log {

    public static void debug(@Nullable String tag, @Nullable String message) {
        android.util.Log.d(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void info(@Nullable String tag, @Nullable String message) {
        android.util.Log.i(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void warn(@Nullable String tag, @Nullable String message) {
        android.util.Log.w(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void warn(@Nullable String tag, @Nullable String message, @NonNull Throwable throwable) {
        android.util.Log.w(tag, message, throwable);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
        FirebaseCrash.report(throwable);
    }

    public static void error(@Nullable String tag, @Nullable String message) {
        android.util.Log.e(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void error(@Nullable String tag, @Nullable String message, @NonNull Throwable throwable) {
        android.util.Log.e(tag, message, throwable);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
        FirebaseCrash.report(throwable);
    }
}
