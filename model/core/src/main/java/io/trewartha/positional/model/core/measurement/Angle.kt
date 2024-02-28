package io.trewartha.positional.model.core.measurement

/**
 * Angle abstraction
 */
sealed interface Angle {

    /**
     * Converts this angle to degrees
     */
    fun inDegrees(): Degrees

    /**
     * Adds another angle to this angle
     */
    operator fun plus(other: Angle): Angle
}
