package io.trewartha.positional.storage

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import io.trewartha.positional.common.Log
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint

class FirestoreTrackDao : TrackDao {

    companion object {
        const val BATCH_SIZE_TRACK_POINT_DELETE = 100
        const val TAG = "FirestoreTrackStorage"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("No one is signed in")
    private val userTracksRef = firestore.collection("users").document(userId).collection("tracks")

    @WorkerThread
    override fun deleteTrack(track: Track): Boolean {
        val trackId = track.start.toString()
        val trackRef = userTracksRef.document(trackId)
        val deleteTask = trackRef.deleteCollection("points", BATCH_SIZE_TRACK_POINT_DELETE)
                .continueWithTask {
                    trackRef.delete()
                }
        Tasks.await(deleteTask)
        return deleteTask.isSuccessful
    }

    override fun getLiveTrack(id: String): LiveData<Track> = TrackLiveData(id)

    override fun getLiveTracks(): LiveData<List<Track>> = TracksLiveData()

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

    private inner class TrackLiveData(id: String) : LiveData<Track>() {

        private val userTrackRef = userTracksRef.document(id)

        private var queryListener: ListenerRegistration? = null

        override fun onActive() {
            queryListener = userTrackRef.addSnapshotListener listener@ { documentSnapshot, exception ->
                if (exception != null) {
                    Log.warn(TAG, "Something went wrong while listening to the track snapshot", exception)
                    return@listener
                }

                value = Track().reify(documentSnapshot)
            }
        }

        override fun onInactive() {
            queryListener?.remove()
        }
    }

    private inner class TracksLiveData : LiveData<List<Track>>() {

        private val userTracksQuery = userTracksRef
                .orderBy("start", Query.Direction.DESCENDING)

        private var queryListener: ListenerRegistration? = null

        override fun onActive() {
            queryListener = userTracksQuery.addSnapshotListener listener@ { querySnapshot, exception ->
                if (exception != null) {
                    Log.warn(TAG, "Something went wrong while listening to the tracks query", exception)
                    return@listener
                }

                value = querySnapshot.documents.map { Track().reify(it) }.filter { it.end != null }
            }
        }

        override fun onInactive() {
            queryListener?.remove()
        }
    }
}