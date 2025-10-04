package io.trewartha.positional.settings

/**
 * Visibility of location accuracy information
 */
public enum class LocationAccuracyVisibility {

    /**
     * Show location accuracy in the UI if possible (API 26+)
     */
    SHOW,

    /**
     * Hide location accuracy in the UI
     */
    HIDE
}
