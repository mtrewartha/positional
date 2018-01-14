package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import io.trewartha.positional.storage.TrackDao
import io.trewartha.positional.tracks.Track

class TracksViewModel(private val trackDao: TrackDao) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    val tracks: LiveData<PagedList<Track>> = LivePagedListBuilder(
            trackDao.getTracks(),
            PAGE_SIZE
    ).build()

    fun deleteTrack(track: Track) = trackDao.deleteTracks(track)
}