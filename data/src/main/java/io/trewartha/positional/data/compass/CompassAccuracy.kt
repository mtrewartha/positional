package io.trewartha.positional.data.compass

/**
 * Accuracy of a compass reading
 */
enum class CompassAccuracy {

    /**
     * Compass readings with this accuracy are not usable and should never be trusted
     */
    UNUSABLE,

    /**
     * Compass readings with this accuracy are unreliable and should rarely be trusted
     */
    UNRELIABLE,

    /**
     * Compass readings with this accuracy are usable but should be treated with caution
     */
    LOW,

    /**
     * Compass readings with this accuracy are usable and can be trusted
     */
    MEDIUM,

    /**
     * Compass readings with this accuracy are very accurate and can be trusted
     */
    HIGH
}
