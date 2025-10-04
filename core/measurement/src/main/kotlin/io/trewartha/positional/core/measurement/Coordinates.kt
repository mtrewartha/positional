package io.trewartha.positional.core.measurement

/**
 * Geographic coordinates abstraction
 */
public sealed interface Coordinates {

    /**
     * Convert the coordinates to geodetic coordinates
     *
     * @return geodetic coordinates that represent the same location on Earth
     */
    public fun asGeodeticCoordinates(): GeodeticCoordinates

    /**
     * Convert the coordinates to MGRS coordinates
     *
     * @return MGRS coordinates that represent the same location on Earth
     */
    public fun asMgrsCoordinates(): MgrsCoordinates

    /**
     * Convert the coordinates to UTM coordinates
     *
     * @return UTM coordinates that represent the same location on Earth or `null` if the
     * coordinates are too close to a pole to be represented in the UTM system
     */
    public fun asUtmCoordinates(): UtmCoordinates?
}
