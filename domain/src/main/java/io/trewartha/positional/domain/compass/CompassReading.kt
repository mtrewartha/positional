package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassAzimuth
import io.trewartha.positional.data.measurement.Angle

/**
 * Wrapper class for a compass reading
 *
 * @property magneticAzimuth Compass azimuth relative to magnetic north
 * @property magneticDeclination Magnetic declination or `null` if unknown. This is the difference
 * between magnetic north and true north. A positive angle indicates magnetic north is east of true
 * north.
 */
data class CompassReading(
    val magneticAzimuth: CompassAzimuth,
    val magneticDeclination: Angle?
) {

    /**
     * Compass azimuth relative to true north or `null` if unknown because the magnetic declination
     * is unknown
     */
    val trueAzimuth: CompassAzimuth? = magneticDeclination?.let { declination ->
        CompassAzimuth(
            angle = magneticAzimuth.angle + declination,
            accelerometerAccuracy = magneticAzimuth.accelerometerAccuracy,
            magnetometerAccuracy = magneticAzimuth.magnetometerAccuracy
        )
    }
}
