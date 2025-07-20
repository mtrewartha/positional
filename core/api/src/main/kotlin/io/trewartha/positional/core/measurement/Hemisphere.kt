package io.trewartha.positional.core.measurement

/**
 * Hemisphere of Earth
 */
enum class Hemisphere {

    /**
     * Northern hemisphere
     */
    NORTH,

    /**
     * Southern hemisphere
     */
    SOUTH;

    override fun toString(): String = when (this) {
        NORTH -> "N"
        SOUTH -> "S"
    }
}
