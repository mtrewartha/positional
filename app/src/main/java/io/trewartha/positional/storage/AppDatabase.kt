package io.trewartha.positional.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint


@Database(
        entities = arrayOf(
                Track::class,
                TrackPoint::class
        ),
        version = 1
)
@TypeConverters(
        Converters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}