package io.trewartha.positional.map.tracking

interface TrackingListener {

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [track] has been started. [track] will have a single, empty [TrackSegment].
     */
    fun onTrackingStarted(track: Track)

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [point] has been added to the last [TrackSegment] of [track].
     */
    fun onTrackPointAdded(track: Track, point: TrackPoint)

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [track] has been stopped. The last [TrackSegment] in [track] won't get any more
     * [TrackPoint]s.
     */
    fun onTrackingStopped(track: Track)
}