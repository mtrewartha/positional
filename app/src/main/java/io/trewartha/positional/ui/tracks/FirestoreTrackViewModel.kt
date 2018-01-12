package io.trewartha.positional.ui.tracks

import io.trewartha.positional.storage.FirestoreTrackDao

class FirestoreTrackViewModel : TrackViewModel() {

    private val trackStorage by lazy { FirestoreTrackDao() }

    override fun getLiveTrack(id: String) = trackStorage.getLiveTrack(id)
}