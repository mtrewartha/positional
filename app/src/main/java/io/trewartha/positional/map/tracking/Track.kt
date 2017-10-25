package io.trewartha.positional.map.tracking

import io.trewartha.positional.common.durationSince
import io.trewartha.positional.time.Duration
import org.threeten.bp.Instant

class Track(val start: Instant) {

    /**
     * The distance (in meters) that this track covers. This distance is the sum of all the
     * [TrackSegment]' distances, which are in turn the sums of distances between the segments'
     * [TrackPoint]s. As such, this distance does not represent an "as the crow flies" distance, but
     * rather a travelled distance.
     */
    var distance: Float = 0.0f
        get() = segments.map { it.distance }.sum()

    /**
     * The [Duration] of time that this track spans
     */
    var duration: Duration = durationSince(start)
        get() = durationSince(start)

    private val segments = mutableListOf(TrackSegment())

    /**
     * Adds a new [TrackPoint] to the last [TrackSegment] of this track
     */
    fun addPoint(point: TrackPoint) {
        segments.last().addPoint(point)
    }

    /**
     * Ends the last [TrackSegment] and adds a new one, to which more [TrackPoint]s can be added
     */
    fun startNewSegment() {
        segments.add(TrackSegment())
    }
}