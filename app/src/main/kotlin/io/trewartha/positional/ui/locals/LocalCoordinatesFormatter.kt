package io.trewartha.positional.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import io.trewartha.positional.ui.utils.format.coordinates.CoordinatesFormatter

val LocalCoordinatesFormatter = staticCompositionLocalOf<CoordinatesFormatter> {
    error("No coordinates formatter has been specified")
}
