package io.trewartha.positional.tracks

import android.location.Location
import org.threeten.bp.Instant

class TrackPoint() {

    var accuracy = 0.0f
    var bearing = 0.0f
    var altitude = 0.0
    var latitude = 0.0
    var longitude = 0.0
    var speed = 0.0f
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