package io.trewartha.positional.core.ui.locals

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.format.SystemDateTimeFormatter
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

public val LocalDateTimeFormatter: ProvidableCompositionLocal<DateTimeFormatter> =
    staticCompositionLocalOf { DefaultDateTimeFormatter }

public val DefaultDateTimeFormatter: DateTimeFormatter =
    SystemDateTimeFormatter(
        clock = Clock.System,
        locale = checkNotNull(LocaleListCompat.getDefault()[0]),
        timeZone = TimeZone.currentSystemDefault(),
    )
