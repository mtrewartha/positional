package io.trewartha.positional.tracks

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import org.threeten.bp.Instant
import java.util.*

@Entity(tableName = "tracks")
class Track {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: UUID = UUID.randomUUID()

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "end")
    var end: Instant? = null

    @ColumnInfo(name = "snapshot_remote")
    var snapshotRemote: Uri? = null

    @ColumnInfo(name = "snapshot_local")
    var snapshotLocal: Uri? = null

    @ColumnInfo(name = "start")
    var start: Instant? = null

    @ColumnInfo(name = "distance")
    var distance: Float = 0.0f

    fun start() {
        start = Instant.now()
    }

    fun stop() {
        end = Instant.now()
    }
}