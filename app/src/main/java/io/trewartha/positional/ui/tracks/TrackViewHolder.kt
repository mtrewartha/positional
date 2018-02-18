package io.trewartha.positional.ui.tracks

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.trewartha.positional.R
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.common.TrackUiHelper
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle


class TrackViewHolder(
        itemView: View,
        private val onTrackClickListener: (Int, Track) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val context = itemView.context

    private val defaultName by lazy { itemView.context.getString(R.string.track_default_name) }
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    private val startTextView: TextView = itemView.findViewById(R.id.startTextView)
    private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)

    @SuppressLint("CheckResult")
    fun bind(track: Track) {
        TrackUiHelper.setImage(context, track, imageView)

        itemView.setOnClickListener { onTrackClickListener(adapterPosition, track) }

        nameTextView.text = if (track.name.isNullOrBlank()) defaultName else track.name
        startTextView.text = buildStartText(track)
        durationTextView.text = TrackUiHelper.getDuration(context, track)
    }

    private fun buildStartText(track: Track): String {
        return if (track.end == null) {
            context.getString(R.string.track_card_duration_currently_tracking)
        } else {
            val start = LocalDateTime.ofInstant(track.start, ZoneId.systemDefault())
            context.getString(
                    R.string.track_card_start_end_format,
                    start.format(dateFormatter),
                    start.format(timeFormatter)
            )
        }
    }
}