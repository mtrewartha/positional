package io.trewartha.positional.model.settings

/**
 * Geographic coordinates formats
 */
enum class CoordinatesFormat {

    /**
     * Decimal degrees
     */
    DD,

    /**
     * Degrees and decimal minutes
     */
    DDM,

    /**
     * Degrees, minutes, and seconds
     */
    DMS,

    /**
     * Military Grid Reference System
     */
    MGRS,

    /**
     * Universal Transverse Mercator
     */
    UTM
}
