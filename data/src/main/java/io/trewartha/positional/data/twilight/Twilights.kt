package io.trewartha.positional.data.twilight

import kotlinx.datetime.LocalTime

/**
 * Local times of various levels of twilight
 *
 * @property horizonTwilight The local time when the sun is at the horizon or `null` if none
 * @property civilTwilight The local time when the sun is 6° below the horizon or `null` if none
 * @property nauticalTwilight The local time when the sun is 12° below the horizon or `null` if none
 * @property astronomicalTwilight The local time when the sun is 18° below the horizon or `null` if
 * none
 */
data class Twilights(
    val horizonTwilight: LocalTime?,
    val civilTwilight: LocalTime?,
    val nauticalTwilight: LocalTime?,
    val astronomicalTwilight: LocalTime?
)
