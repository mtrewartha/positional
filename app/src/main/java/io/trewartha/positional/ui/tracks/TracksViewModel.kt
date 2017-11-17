package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import io.trewartha.positional.tracks.Track

abstract class TracksViewModel : ViewModel() {

    abstract fun getTracks(): LiveData<List<Track>>

    abstract fun deleteTrack(track: Track): Task<Void>
}