package io.trewartha.positional.storage

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import io.trewartha.positional.tracks.Track
import org.threeten.bp.Instant

fun Track.toMap(): Map<String, Any> {
    return HashMap<String, Any>(5).apply {
        snapshot?.let { put("snapshot", it.toString()) }
        name?.let { put("name", it) }
        start?.let { put("start", it.toString()) }
        end?.let { put("end", it.toString()) }
        put("distance", distance)
    }
}

fun Track.reify(documentSnapshot: DocumentSnapshot): Track {
    return Track().apply {
        snapshot = (documentSnapshot["snapshot"] as String?)?.let { Uri.parse(it) }
        name = documentSnapshot["name"] as String?
        start = (documentSnapshot["start"] as String?)?.let { Instant.parse(it) }
        end = (documentSnapshot["end"] as String?)?.let { Instant.parse(it) }
        distance = (documentSnapshot["distance"] as Double?)?.toFloat() ?: 0.0f
    }
}