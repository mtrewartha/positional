package io.trewartha.positional;

import com.google.firebase.crash.FirebaseCrash;

public class Log {

    public static void debug(String tag, String message) {
        android.util.Log.d(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void info(String tag, String message) {
        android.util.Log.i(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void warn(String tag, String message) {
        android.util.Log.w(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void warn(String tag, String message, Throwable throwable) {
        android.util.Log.w(tag, message, throwable);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void error(String tag, String message) {
        android.util.Log.e(tag, message);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }

    public static void error(String tag, String message, Throwable throwable) {
        android.util.Log.e(tag, message, throwable);
        FirebaseCrash.log(String.format("%s - %s", tag, message));
    }
}
