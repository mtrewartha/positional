package io.trewartha.positional.ui.location.locals

import androidx.compose.runtime.staticCompositionLocalOf
import io.trewartha.positional.ui.location.format.CoordinatesFormatter

val LocalCoordinatesFormatter = staticCompositionLocalOf<CoordinatesFormatter> {
    error("No coordinates formatter has been specified")
}
