package io.trewartha.positional.location.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import io.trewartha.positional.location.ui.format.CoordinatesFormatter

val LocalCoordinatesFormatter = staticCompositionLocalOf<CoordinatesFormatter> {
    error("No coordinates formatter has been specified")
}
