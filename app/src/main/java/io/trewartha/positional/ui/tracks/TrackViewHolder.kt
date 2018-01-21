package io.trewartha.positional.ui.tracks

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.time.Duration
import io.trewartha.positional.tracks.Track
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle


class TrackViewHolder(
        itemView: View,
        private val onTrackClickListener: (Int, Track) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val context = itemView.context
    private val resources = context.resources

    private val defaultName by lazy { itemView.context.getString(R.string.track_default_name) }
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    private val startTextView: TextView = itemView.findViewById(R.id.startTextView)
    private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)

    @SuppressLint("CheckResult")
    fun bind(track: Track) {
        val snapshotUri = track.imageLocal ?: track.imageRemote
        val errorDrawable = context.getDrawable(R.drawable.ic_terrain_black_24dp).apply {
            setTint(ContextCompat.getColor(context, R.color.gray4))
        }
        GlideApp.with(context)
                .load(snapshotUri)
                .error(errorDrawable)
                .centerCrop()
                .into(imageView)

        itemView.setOnClickListener { onTrackClickListener(adapterPosition, track) }

        nameTextView.text = if (track.name.isNullOrBlank()) defaultName else track.name
        startTextView.text = buildStartText(track)
        durationTextView.text = buildDurationText(track)
    }

    private fun buildDurationText(track: Track): String {
        val trackStart = track.start ?: return ""
        val trackEnd = track.end ?: return ""
        return Duration.between(trackStart, trackEnd).let {
            val hrs = resources.getQuantityString(
                    R.plurals.track_card_duration_hours,
                    it.hours,
                    it.hours
            )
            val mins = resources.getQuantityString(
                    R.plurals.track_card_duration_minutes,
                    it.minutes,
                    it.minutes
            )
            val seconds = resources.getQuantityString(
                    R.plurals.track_card_duration_seconds,
                    it.seconds,
                    it.seconds
            )
            when {
                it.hours > 0 -> {
                    context.getString(R.string.track_card_duration_format_two, hrs, mins)
                }
                it.minutes > 0 -> {
                    context.getString(R.string.track_card_duration_format_two, mins, seconds)
                }
                else -> {
                    context.getString(R.string.track_card_duration_format_one, seconds)
                }
            }
        }
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