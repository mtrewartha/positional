package io.trewartha.positional.core.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.format.SystemDateTimeFormatter

val LocalDateTimeFormatter = staticCompositionLocalOf<DateTimeFormatter> {
    val locale = checkNotNull(LocaleListCompat.getDefault()[0])
    SystemDateTimeFormatter(locale)
}
