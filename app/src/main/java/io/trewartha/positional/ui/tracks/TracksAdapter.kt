package io.trewartha.positional.ui.tracks

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.recyclerview.extensions.DiffCallback
import android.view.LayoutInflater
import android.view.ViewGroup
import io.trewartha.positional.R
import io.trewartha.positional.tracks.Track

class TracksAdapter(
        private val onTrackClickListener: (Int, Track) -> Unit
) : PagedListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = currentList?.get(position) ?: return
        holder.bind(track)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.track, parent, false)
        return TrackViewHolder(itemView, onTrackClickListener)
    }

    private class TrackDiffCallback : DiffCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }
}