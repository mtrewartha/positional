package io.trewartha.positional.storage

import android.support.annotation.WorkerThread
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint

class FirebaseTrackStorage : TrackStorage {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("No one is signed in")
    private val userTracksRef = firestore.collection("users").document(userId).collection("tracks")

    @WorkerThread
    override fun createTrack(track: Track): Boolean {
        val createTask = getTrackRef(track).set(track.toMap())
        Tasks.await(createTask)
        return createTask.isSuccessful
    }

    @WorkerThread
    override fun saveTrack(track: Track): Boolean {
        val updateTask = getTrackRef(track).set(track.toMap())
        Tasks.await(updateTask)
        return updateTask.isSuccessful
    }

    @WorkerThread
    override fun saveTrackPoint(track: Track, point: TrackPoint): Boolean {
        val addPointTask = getTrackPointRef(track, point).set(point.toMap())
        Tasks.await(addPointTask)
        return addPointTask.isSuccessful
    }

    private fun getTrackRef(track: Track) = userTracksRef.document(track.start.toString())

    private fun getTrackPointRef(track: Track, point: TrackPoint) = getTrackRef(track)
            .collection("points")
            .document(point.time.toString())
}