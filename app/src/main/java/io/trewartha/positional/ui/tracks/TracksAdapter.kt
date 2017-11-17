package io.trewartha.positional.ui.tracks

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.trewartha.positional.R
import io.trewartha.positional.tracks.Track

class TracksAdapter(
        private val onTrackClickListener: (Int, Track) -> Unit,
        private val onTrackEditListener: (Int, Track) -> Unit,
        private val onTrackDeleteListener: (Int, Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = mutableListOf<Track>()

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.track_card, parent, false)
        return TrackViewHolder(
                itemView,
                onTrackClickListener,
                onTrackEditListener,
                onTrackDeleteListener
        )
    }
}