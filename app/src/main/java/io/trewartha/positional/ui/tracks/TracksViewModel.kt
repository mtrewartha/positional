package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import io.trewartha.positional.storage.TrackDao
import io.trewartha.positional.tracks.Track

class TracksViewModel(trackDao: TrackDao) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    val tracks: LiveData<PagedList<Track>> = LivePagedListBuilder(
            trackDao.getTracks("end DESC"),
            PAGE_SIZE
    ).build()
}