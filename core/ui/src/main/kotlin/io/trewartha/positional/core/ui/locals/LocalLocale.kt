package io.trewartha.positional.core.ui.locals

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import java.util.Locale

/** The [Locale] composables should use for formatting */
public val LocalLocale: ProvidableCompositionLocal<Locale> =
    staticCompositionLocalOf { checkNotNull(LocaleListCompat.getDefault()[0]) }
