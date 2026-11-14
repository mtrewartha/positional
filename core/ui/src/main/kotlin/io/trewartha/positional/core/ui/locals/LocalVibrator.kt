package io.trewartha.positional.core.ui.locals

import android.os.Vibrator
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/** The [Vibrator] available to composables, or null if the device has none */
public val LocalVibrator: ProvidableCompositionLocal<Vibrator?> =
    staticCompositionLocalOf { error("No vibrator has been specified") }
