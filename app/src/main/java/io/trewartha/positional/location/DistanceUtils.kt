package io.trewartha.positional.location

import android.location.Location
import io.trewartha.positional.tracks.TrackPoint

object DistanceUtils {

    private const val FEET_PER_METER = 3.2808399200439453f
    private const val MPH_PER_MPS = 2.236936f
    private const val KPH_PER_MPS = 3.6f

    /**
     * Calculates the distance between two [TrackPoint]s in meters
     *
     * @param trackPoint1 The first [TrackPoint] to use in the distance calculation
     * @param trackPoint2 The second [TrackPoint] to use in the distance calculation
     *
     * @return The distance between the two given [TrackPoint]s in meters
     */
    fun distanceBetween(trackPoint1: TrackPoint, trackPoint2: TrackPoint): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
                trackPoint1.latitude, trackPoint1.longitude,
                trackPoint2.latitude, trackPoint2.longitude,
                results
        )
        return results[0]
    }

    fun distanceInMiles(meters: Float) = meters / 1609.34f

    fun distanceInKilometers(meters: Float) = meters / 1000.0f

    fun metersToFeet(meters: Float) = meters * FEET_PER_METER

    fun metersPerSecondToMilesPerHour(metersPerSecond: Float) = metersPerSecond * MPH_PER_MPS

    fun metersPerSecondToKilometersPerHour(metersPerSecond: Float) = metersPerSecond * KPH_PER_MPS
}