package io.trewartha.positional.tracks

import android.location.Location

class TrackSegment {

    /**
     * The distance (in meters) that this [TrackSegment] covers. This distance is the sum of the
     * distances between all of this segment's [TrackPoint]s. As such, this distance does not
     * represent an "as the crow flies" distance, but rather a travelled distance.
     */
    var distance = 0.0f
        private set

    private val trackPoints = mutableListOf<TrackPoint>()

    /**
     * Adds a new [TrackPoint] to this [TrackSegment]. This [TrackSegment]'s distance will include
     * the distance to the added point after this function is called.
     */
    fun addPoint(trackPoint: TrackPoint) {
        if (trackPoints.size > 1) {
            distance += calculateDistance(trackPoints.last(), trackPoint)
        }
        trackPoints.add(trackPoint)
    }

    fun getPoints(): List<TrackPoint> {
        return trackPoints.toList()
    }

    private fun calculateDistance(trackPoint1: TrackPoint, trackPoint2: TrackPoint): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
                trackPoint1.latitude, trackPoint1.longitude,
                trackPoint2.latitude, trackPoint2.longitude,
                results
        )
        return results[0]
    }
}