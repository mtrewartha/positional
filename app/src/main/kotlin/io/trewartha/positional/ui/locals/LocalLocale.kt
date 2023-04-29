package io.trewartha.positional.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat

val LocalLocale = staticCompositionLocalOf { checkNotNull(LocaleListCompat.getDefault()[0]) }
