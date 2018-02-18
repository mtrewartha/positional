package io.trewartha.positional.storage

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint
import java.util.*

@Dao
interface TrackDao {

    // Tracks

    @Insert
    fun createTrack(vararg tracks: Track)

    @Query("SELECT * FROM tracks WHERE id = :id LIMIT 1")
    fun getTrack(id: UUID): Flowable<Track>

    @Query("SELECT * FROM tracks WHERE end IS NOT NULL ORDER BY :orderBy")
    fun getTracks(orderBy: String): DataSource.Factory<Int, Track>

    @Query("SELECT * FROM track_points WHERE track_id = :trackId ORDER BY time")
    fun getTrackPoints(trackId: UUID): Flowable<List<TrackPoint>>

    @Update
    fun updateTracks(vararg tracks: Track): Int

    @Delete
    fun deleteTracks(vararg tracks: Track): Int

    // Track Points

    @Insert
    fun createTrackPoint(vararg trackPoints: TrackPoint)

}