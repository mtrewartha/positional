package io.trewartha.positional.storage

import com.google.firebase.firestore.DocumentSnapshot
import io.trewartha.positional.tracks.TrackPoint
import org.threeten.bp.Instant

fun TrackPoint.toMap(): Map<String, Any> {
    return HashMap<String, Any>(7).apply {
        put("accuracy", accuracy)
        put("bearing", bearing)
        put("altitude", altitude)
        put("latitude", latitude)
        put("longitude", longitude)
        put("speed", speed)
        put("time", time.toString())
    }
}

fun TrackPoint.reify(documentSnapshot: DocumentSnapshot): TrackPoint {
    return TrackPoint().apply {
        accuracy = (documentSnapshot["accuracy"] as Double).toFloat()
        bearing = (documentSnapshot["bearing"] as Double).toFloat()
        altitude = documentSnapshot["altitude"] as Double
        latitude = documentSnapshot["latitude"] as Double
        longitude = documentSnapshot["longitude"] as Double
        speed = (documentSnapshot["speed"] as Double).toFloat()
        time = Instant.parse(documentSnapshot["time"] as String)
    }
}