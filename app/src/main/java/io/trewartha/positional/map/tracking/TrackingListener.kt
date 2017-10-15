package io.trewartha.positional.map.tracking

interface TrackingListener {

    fun onTrackingUnavailable()
    fun onTrackingStarted(track: Track)
    fun onTrackPointAdded(track: Track, point: TrackPoint)
    fun onTrackingTemporarilyUnavailable()
    fun onTrackingResumed()
    fun onTrackingStopped(track: Track)
    fun onLocationDisabled()
}