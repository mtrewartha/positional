package io.trewartha.positional.storage

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import android.content.Context
import io.trewartha.positional.ui.track.TrackViewModel
import io.trewartha.positional.ui.tracks.TracksViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    companion object {
        lateinit var trackDao: TrackDao

        private lateinit var database: AppDatabase

        fun initialize(context: Context) {
            database = Room.databaseBuilder(context, AppDatabase::class.java, "positional").build()
            trackDao = database.trackDao()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TrackViewModel::class.java -> TrackViewModel(trackDao)
            TracksViewModel::class.java -> TracksViewModel(trackDao)
            else -> throw IllegalArgumentException(
                    "Don't know how to create a view model for type ${modelClass.name}"
            )
        } as T
    }
}