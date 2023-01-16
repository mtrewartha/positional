package io.trewartha.positional.data.twilight

import kotlinx.datetime.Instant

/**
 * Times of various levels of twilight
 *
 * @property horizonTwilight The instant when the sun is at the horizon or `null` if none
 * @property civilTwilight The instant when the sun is 6° below the horizon or `null` if none
 * @property nauticalTwilight The instant when the sun is 12° below the horizon or `null` if none
 * @property astronomicalTwilight The instant when the sun is 18° below the horizon or `null` if
 * none
 */
data class Twilights(
    val horizonTwilight: Instant?,
    val civilTwilight: Instant?,
    val nauticalTwilight: Instant?,
    val astronomicalTwilight: Instant?
)
