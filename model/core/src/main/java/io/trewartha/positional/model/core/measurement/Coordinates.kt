package io.trewartha.positional.model.core.measurement

/**
 * Geographic coordinates abstraction
 */
sealed interface Coordinates {

    /**
     * Convert the coordinates to geodetic coordinates
     */
    fun asGeodeticCoordinates(): GeodeticCoordinates

    /**
     * Convert the coordinates to MGRS coordinates
     */
    fun asMgrsCoordinates(): MgrsCoordinates

    /**
     * Convert the coordinates to UTM coordinates
     */
    fun asUtmCoordinates(): UtmCoordinates
}
