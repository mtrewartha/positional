package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import io.trewartha.positional.common.Log
import io.trewartha.positional.storage.deleteCollection
import io.trewartha.positional.storage.reify
import io.trewartha.positional.tracks.Track

class FirestoreTracksViewModel : TracksViewModel() {

    companion object {
        const val TAG = "TracksViewModel"
        const val BATCH_SIZE_TRACK_POINT_DELETE = 100
    }

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid ?: throw IllegalStateException("No one is signed in")
    private val tracksLiveData by lazy { TracksLiveData() }
    private val userTracksRef = firestore.collection("users").document(userId).collection("tracks")

    private val pendingDeletes = HashSet<String>()

    override fun getTracks(): LiveData<List<Track>> = tracksLiveData

    override fun deleteTrack(track: Track): Task<Void> {
        val trackId = track.start.toString()
        pendingDeletes.add(trackId)
        val trackRef = userTracksRef.document(trackId)
        return trackRef.deleteCollection("points", BATCH_SIZE_TRACK_POINT_DELETE)
                .continueWithTask {
                    trackRef.delete()
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

                value = querySnapshot.documents.map { Track().reify(it) }.filter {
                    // Don't report changes to any that were already deleted from the view
                    !pendingDeletes.remove(it.start.toString()) && it.end != null
                }
            }
        }

        override fun onInactive() {
            queryListener?.remove()
        }
    }
}