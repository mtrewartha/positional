package io.trewartha.positional.map.tracking

interface TrackingListener {

    fun onTrackingStarted(track: Track)
    fun onTrackingResumed(track: Track)
    fun onTrackPointAdded(track: Track, point: TrackPoint)
    fun onTrackingStopped(track: Track)
}