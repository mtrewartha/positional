package io.trewartha.positional.map.tracking

class Track {

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