package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import io.trewartha.positional.tracks.Track

abstract class TrackViewModel : ViewModel() {

    /**
     * Gets a [LiveData] of the [Track] with ID [id] that the view can observe
     *
     * @return A [LiveData] of the [Track] with ID [id] that the view can observe
     */
    abstract fun getLiveTrack(id: String): LiveData<Track>
}