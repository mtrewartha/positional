package io.trewartha.positional.tracks

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import android.location.Location
import org.threeten.bp.Instant
import java.util.*

@Entity(
        tableName = "track_points",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Track::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("track_id"),
                        onDelete = CASCADE
                )
        ),
        indices = arrayOf(
                Index("track_id")
        )
)
class TrackPoint() {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: UUID = UUID.randomUUID()

    @ColumnInfo(name = "track_id")
    var trackId: UUID = UUID.randomUUID()

    @ColumnInfo(name = "accuracy")
    var accuracy = 0.0f

    @ColumnInfo(name = "bearing")
    var bearing = 0.0f

    @ColumnInfo(name = "altitude")
    var altitude = 0.0

    @ColumnInfo(name = "latitude")
    var latitude = 0.0

    @ColumnInfo(name = "longitude")
    var longitude = 0.0

    @ColumnInfo(name = "speed")
    var speed = 0.0f

    @ColumnInfo(name = "time")
    var time: Instant = Instant.now()

    constructor(location: Location) : this() {
        accuracy = location.accuracy
        bearing = location.bearing
        altitude = location.altitude
        latitude = location.latitude
        longitude = location.longitude
        speed = location.speed
        time = Instant.ofEpochMilli(location.time)
    }
}