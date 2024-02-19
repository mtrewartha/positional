package io.trewartha.positional.ui.design.locals

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import io.trewartha.positional.ui.core.format.DateTimeFormatter
import io.trewartha.positional.ui.core.format.SystemDateTimeFormatter

val LocalDateTimeFormatter = staticCompositionLocalOf<DateTimeFormatter> {
    val locale = checkNotNull(LocaleListCompat.getDefault()[0])
    SystemDateTimeFormatter(locale)
}
