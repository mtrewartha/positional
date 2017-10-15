package io.trewartha.positional.map.tracking

import android.location.Location

class TrackSegment {

    var distance = 0.0f
        private set

    private val trackPoints = mutableListOf<TrackPoint>()

    fun addPoint(trackPoint: TrackPoint) {
        trackPoints.add(trackPoint)
        if (trackPoints.size > 1) {
            distance += calculateDistance(trackPoints.last(), trackPoint)
        }
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