package io.trewartha.positional.core.ui.locals

import android.os.Vibrator
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalVibrator: ProvidableCompositionLocal<Vibrator?> =
    staticCompositionLocalOf { error("No vibrator has been specified") }
