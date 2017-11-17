package io.trewartha.positional.tracks

import android.net.Uri
import org.threeten.bp.Instant

class Track {

    var name: String? = null
    var end: Instant? = null
    var snapshotRemote: Uri? = null
    var snapshotLocal: Uri? = null
    var start: Instant? = null
    var distance: Float = 0.0f

    /**
     * Starts this [Track], settings its [start] to the current [Instant]
     */
    fun start() {
        start = Instant.now()
    }

    /**
     * Stops this [Track], settings its [end] to the current [Instant]
     */
    fun stop() {
        end = Instant.now()
    }
}