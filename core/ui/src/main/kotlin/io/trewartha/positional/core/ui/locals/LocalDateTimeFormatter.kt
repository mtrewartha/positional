package io.trewartha.positional.core.ui.locals

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.format.SystemDateTimeFormatter
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

public val LocalDateTimeFormatter: ProvidableCompositionLocal<DateTimeFormatter> =
    staticCompositionLocalOf { error("No DateTimeFormatter provided") }

/**
 * A [DateTimeFormatter] for Compose previews, where the app's injected formatter isn't available.
 */
public val PreviewDateTimeFormatter: DateTimeFormatter =
    SystemDateTimeFormatter(
        clock = Clock.System,
        localeProvider = { checkNotNull(LocaleListCompat.getDefault()[0]) },
        timeZoneProvider = { TimeZone.currentSystemDefault() },
    )
