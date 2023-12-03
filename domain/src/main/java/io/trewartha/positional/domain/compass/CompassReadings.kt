package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassAzimuth
import io.trewartha.positional.data.measurement.Angle

data class CompassReadings(
    val magneticAzimuth: CompassAzimuth,
    val magneticDeclination: Angle?
) {
    val trueAzimuth: CompassAzimuth? = magneticDeclination?.let { declination ->
        CompassAzimuth(
            angle = magneticAzimuth.angle + declination,
            accelerometerAccuracy = magneticAzimuth.accelerometerAccuracy,
            magnetometerAccuracy = magneticAzimuth.magnetometerAccuracy
        )
    }
}
