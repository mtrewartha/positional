package io.trewartha.positional.ui.tracks

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import io.trewartha.positional.common.Executors
import io.trewartha.positional.storage.FirestoreTrackDao
import io.trewartha.positional.tracks.Track
import java.util.concurrent.Callable

class FirestoreTracksViewModel : TracksViewModel() {

    private val tracksLiveData by lazy { trackStorage.getLiveTracks() }
    private val trackStorage by lazy { FirestoreTrackDao() }

    override fun deleteTrack(track: Track): Task<Track> {
        return Tasks.call(Executors.STORAGE, Callable {
            trackStorage.deleteTrack(track)
            track
        })
    }

    override fun getLiveTracks() = tracksLiveData
}