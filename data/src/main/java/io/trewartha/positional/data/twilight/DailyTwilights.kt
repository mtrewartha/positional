package io.trewartha.positional.data.twilight

/**
 * Collection of various twilight times for a given date/location combination
 *
 * @property morningTwilights The morning twilight times (when the sun is rising)
 * @property eveningTwilights The morning twilight times (when the sun is setting)
 */
data class DailyTwilights(
    val morningTwilights: Twilights,
    val eveningTwilights: Twilights
)
