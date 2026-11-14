package io.trewartha.positional.location.ui.locals

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import io.trewartha.positional.location.ui.format.CoordinatesFormatter

/** The [CoordinatesFormatter] composables should use to format coordinates */
public val LocalCoordinatesFormatter: ProvidableCompositionLocal<CoordinatesFormatter> =
    staticCompositionLocalOf { error("No coordinates formatter has been specified") }
