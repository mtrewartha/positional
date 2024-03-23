package io.trewartha.positional.ui.design.locals

import android.os.Vibrator
import androidx.compose.runtime.staticCompositionLocalOf

val LocalVibrator = staticCompositionLocalOf<Vibrator?> {
    error("No vibrator has been specified")
}
