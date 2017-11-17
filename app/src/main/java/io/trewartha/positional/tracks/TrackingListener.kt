package io.trewartha.positional.tracks

import io.trewartha.positional.time.Duration

interface TrackingListener {

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [track] has been started.
     *
     * @param track The [Track] that has been started
     */
    fun onTrackingStarted(track: Track)

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [point] has been added to [track].
     *
     * @param track The [Track] that has had [point] added to it
     * @param point The [TrackPoint] that has been added to [track]
     */
    fun onTrackPointAdded(track: Track, point: TrackPoint)

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [track]'s duration has changed.
     *
     * @param track The [Track] whose duration changed
     * @param duration The new [Duration] of [track]
     */
    fun onTrackDurationChanged(track: Track, duration: Duration)

    /**
     * If this listener has been added to the [TrackingService], the service will call this function
     * when [track] has been stopped. [track] won't get any more [TrackPoint]s.
     *
     * @param track The [Track] that has been stopped
     */
    fun onTrackingStopped(track: Track)
}