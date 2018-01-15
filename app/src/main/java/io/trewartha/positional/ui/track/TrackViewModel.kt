package io.trewartha.positional.ui.track

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.trewartha.positional.storage.TrackDao
import io.trewartha.positional.tracks.Track
import java.util.*

class TrackViewModel(private val trackDao: TrackDao) : ViewModel() {

    fun getTrack(id: UUID): Flowable<Track> = trackDao.getTrack(id)

    fun updateTrack(track: Track) = trackDao.updateTracks(track)
}