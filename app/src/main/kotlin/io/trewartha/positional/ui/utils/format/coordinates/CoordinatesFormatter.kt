package io.trewartha.positional.ui.utils.format.coordinates

import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.location.CoordinatesFormat

/**
 * Formats location coordinates into a list of strings for display to the user
 */
interface CoordinatesFormatter {

    /**
     * The [CoordinatesFormat] that this formatter can format coordinates in
     */
    val format: CoordinatesFormat

    /**
     * Formats some given coordinates into a list of strings suitable for display to the user
     *
     * @param coordinates The geographic coordinates to format or `null` if none
     *
     * @return If [coordinates] was null, then a list of `null`s is returned. If [coordinates] was
     * not null, then a list of non-null strings representing the formatted coordinates is returned.
     * The length of the returned list and the meaning behind each string in it are
     * implementation-dependent.
     */
    fun formatForDisplay(coordinates: Coordinates?): List<String?>

    /**
     * Formats some given coordinates into a string suitable for copying/pasting or sharing outside
     * of the app
     */
    fun formatForCopy(coordinates: Coordinates): String

    /**
     * Assumes the string represents a number and normalizes all decimal separators to a period
     */
    fun String.normalizeDecimalSeparator(): String =
        replace(COMMA, PERIOD)
}

private const val COMMA = ','
private const val PERIOD = '.'
