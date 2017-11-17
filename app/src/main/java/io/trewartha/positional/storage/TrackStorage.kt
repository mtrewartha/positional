package io.trewartha.positional.storage

import android.support.annotation.WorkerThread
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint

interface TrackStorage {

    @WorkerThread
    fun createTrack(track: Track): Boolean

    @WorkerThread
    fun saveTrack(track: Track): Boolean

    @WorkerThread
    fun saveTrackPoint(track: Track, point: TrackPoint): Boolean
}