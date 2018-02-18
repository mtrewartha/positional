package io.trewartha.positional.ui.track

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.trewartha.positional.storage.TrackDao
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint
import java.util.*

class TrackViewModel(private val trackDao: TrackDao) : ViewModel() {

    fun deleteTrack(track: Track) = trackDao.deleteTracks(track)

    fun getTrack(id: UUID): Flowable<Track> = trackDao.getTrack(id)

    fun getTrackPoints(trackId: UUID): Flowable<List<TrackPoint>> = trackDao.getTrackPoints(trackId)

    fun updateTrack(track: Track) = trackDao.updateTracks(track)

}