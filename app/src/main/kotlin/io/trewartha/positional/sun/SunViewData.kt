package io.trewartha.positional.sun

import android.location.Location
import org.threeten.bp.Instant

data class SunViewData(
        val location: Location,
        val astronomicalDawn: Instant?,
        val nauticalDawn: Instant?,
        val civilDawn: Instant?,
        val sunrise: Instant?,
        val sunset: Instant?,
        val civilDusk: Instant?,
        val nauticalDusk: Instant?,
        val astronomicalDusk: Instant?
)