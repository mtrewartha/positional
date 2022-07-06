package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable

@Immutable
data class LocationState(
    val coordinates: String,
    val maxLines: Int,
    val coordinatesForCopy: String,
    val stats: Stats,
    val screenLockEnabled: Boolean,
) {

    @Immutable
    data class Stats(
        val accuracy: String,
        val bearing: String,
        val bearingAccuracy: String?,
        val altitude: String,
        val altitudeAccuracy: String?,
        val speed: String,
        val speedAccuracy: String?,
        val showAccuracies: Boolean,
        val updatedAt: String,
    )
}