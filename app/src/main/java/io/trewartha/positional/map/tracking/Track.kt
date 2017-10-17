package io.trewartha.positional.map.tracking

import org.threeten.bp.Instant

class Track(val start: Instant) {

    var distance: Float = 0.0f
        get() = segments.map { it.distance }.sum()

    private val segments = mutableListOf(TrackSegment())

    fun addPoint(point: TrackPoint) {
        segments.last().addPoint(point)
    }

    fun startNewSegment() {
        segments.add(TrackSegment())
    }
}