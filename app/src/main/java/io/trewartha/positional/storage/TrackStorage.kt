package io.trewartha.positional.storage

import android.arch.lifecycle.LiveData
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint

interface TrackStorage {

    /**
     * Deletes a [Track] from the storage medium that the implementer of this interface uses
     *
     * @param track The [Track] that you want to delete from storage
     *
     * @return True if [track] was deleted from storage, false otherwise
     */
    fun deleteTrack(track: Track): Boolean

    /**
     * Gets a [LiveData] that represents the current [Track]s in storage
     *
     * @return A [LiveData] that you can observe to get continual updates to [Track]s in storage
     */
    fun getLiveTracks(): LiveData<List<Track>>

    /**
     * Saves a [Track] to the storage medium that the implementer of this interface uses
     *
     * @param track The [Track] that you want to save to storage
     *
     * @return True if [track] was saved to storage, false otherwise
     */
    fun saveTrack(track: Track): Boolean

    /**
     * Saves a [TrackPoint] for a [Track] to the storage medium that the implementer of this
     * interface uses
     *
     * @param track The [Track] that [point] belongs to
     * @param point The [TrackPoint] that you want to save to storage
     *
     * @return True if [point] was saved to storage, false otherwise
     */
    fun saveTrackPoint(track: Track, point: TrackPoint): Boolean
}