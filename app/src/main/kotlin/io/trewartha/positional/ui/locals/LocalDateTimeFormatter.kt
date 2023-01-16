package io.trewartha.positional.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter

val LocalDateTimeFormatter =
    staticCompositionLocalOf<DateTimeFormatter> { SystemDateTimeFormatter() }
