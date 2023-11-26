package io.trewartha.positional.data.sun

import kotlinx.datetime.LocalTime

/**
 * Collection of various solar times for a given date/location combination
 *
 * @property astronomicalDawn The local morning time when the sun is 18° below the horizon or `null`
 * if the sun does not reach that point for the date/location combination
 * @property nauticalDawn The local morning time when the sun is 12° below the horizon or `null` if
 * the sun does not reach that point for the date/location combination
 * @property civilDawn The local morning time when the sun is 6° below the horizon or `null` if the
 * sun does not reach that point for the date/location combination
 * @property sunrise The local time of sunrise (when the sun is at the horizon) or `null` if the sun
 * does not reach that point for the date/location combination
 * @property sunset The local time of sunset (when the sun is at the horizon) or `null` if the sun
 * does not reach that point for the date/location combination
 * @property civilDusk The local evening time when the sun is 6° below the horizon or `null` if the
 * sun does not reach that point for the date/location combination
 * @property nauticalDusk The local evening time when the sun is 12° below the horizon or `null` if
 * the sun does not reach that point for the date/location combination
 * @property astronomicalDusk The local evening time when the sun is 18° below the horizon or `null`
 * if the sun does not reach that point for the date/location combination
 */
data class SolarTimes(
    val astronomicalDawn: LocalTime?,
    val nauticalDawn: LocalTime?,
    val civilDawn: LocalTime?,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
    val civilDusk: LocalTime?,
    val nauticalDusk: LocalTime?,
    val astronomicalDusk: LocalTime?
)
