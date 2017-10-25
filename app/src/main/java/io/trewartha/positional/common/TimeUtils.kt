package io.trewartha.positional.common

import io.trewartha.positional.time.Duration
import org.threeten.bp.Instant

fun durationSince(instant: Instant): Duration {
    val durationSeconds = org.threeten.bp.Duration.between(instant, Instant.now()).seconds
    val durationMinutes = durationSeconds / 60
    val seconds = (durationSeconds % 60).toInt()
    val minutes = (durationMinutes % 60).toInt()
    val hours = (durationMinutes / 60).toInt()
    return Duration(hours, minutes, seconds)
}