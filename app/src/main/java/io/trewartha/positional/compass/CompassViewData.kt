package io.trewartha.positional.compass

data class CompassViewData(
    val azimuth: Float?,
    val declination: Float?,
    val mode: CompassMode?,
    val accelerometerAccuracy: Int?,
    val magnetometerAccuracy: Int?
)