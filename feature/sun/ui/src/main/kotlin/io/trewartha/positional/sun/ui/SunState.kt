package io.trewartha.positional.sun.ui

import androidx.compose.runtime.Immutable
import io.trewartha.positional.core.ui.State
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * The solar times for [selectedDate]
 *
 * @property todaysDate the current date
 * @property selectedDate the date the solar times are for
 * @property astronomicalDawn start of astronomical twilight before sunrise
 * @property nauticalDawn start of nautical twilight before sunrise
 * @property civilDawn start of civil twilight before sunrise
 * @property sunrise time the sun rises
 * @property sunset time the sun sets
 * @property civilDusk end of civil twilight after sunset
 * @property nauticalDusk end of nautical twilight after sunset
 * @property astronomicalDusk end of astronomical twilight after sunset
 */
@Immutable
public data class SunState(
    val todaysDate: LocalDate,
    val selectedDate: LocalDate,
    val astronomicalDawn: State<LocalTime?, Nothing>,
    val nauticalDawn: State<LocalTime?, Nothing>,
    val civilDawn: State<LocalTime?, Nothing>,
    val sunrise: State<LocalTime?, Nothing>,
    val sunset: State<LocalTime?, Nothing>,
    val civilDusk: State<LocalTime?, Nothing>,
    val nauticalDusk: State<LocalTime?, Nothing>,
    val astronomicalDusk: State<LocalTime?, Nothing>
)
