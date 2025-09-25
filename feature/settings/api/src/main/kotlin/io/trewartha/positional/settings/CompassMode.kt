package io.trewartha.positional.settings

/**
 * Mode of a compass that indicates whether the azimuth is relative to true north or magnetic north
 */
public enum class CompassMode {

    /**
     * A compass in this mode or a compass reading with this mode has an azimuth relative to true
     * (i.e. geographic) north
     */
    TRUE_NORTH,

    /**
     * A compass in this mode or a compass reading with this mode has an azimuth relative to
     * magnetic north
     */
    MAGNETIC_NORTH
}
