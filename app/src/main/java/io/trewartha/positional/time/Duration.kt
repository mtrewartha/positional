package io.trewartha.positional.time

import org.threeten.bp.Instant

data class Duration(val hours: Int, val minutes: Int, val seconds: Int) {

    companion object {

        fun between(start: Instant, end: Instant): Duration {
            val durationSeconds = org.threeten.bp.Duration.between(start, end).seconds
            val durationMinutes = durationSeconds / 60
            val seconds = (durationSeconds % 60).toInt()
            val minutes = (durationMinutes % 60).toInt()
            val hours = (durationMinutes / 60).toInt()
            return Duration(hours, minutes, seconds)
        }
    }
}