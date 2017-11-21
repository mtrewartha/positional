package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import io.trewartha.positional.tracks.Track

abstract class TracksViewModel : ViewModel() {

    /**
     * Gets a [LiveData] of [Track]s that the view can observe
     *
     * @return A [LiveData] of [Track]s that the view can observe
     */
    abstract fun getLiveTracks(): LiveData<List<Track>>

    /**
     * Deletes [track] from whatever underlying storage is used by this view model
     *
     * @param track The [Track] to delete
     *
     * @return The deleted [Track]
     */
    abstract fun deleteTrack(track: Track): Task<Track>
}