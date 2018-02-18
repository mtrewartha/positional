package io.trewartha.positional.ui.common

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.time.Duration
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.DayNightThemeUtils

object TrackUiHelper {

    fun setDistance(context: Context, track: Track): String {
        val useMetricUnits = context.getSharedPreferences(
                context.getString(R.string.settings_filename),
                Context.MODE_PRIVATE
        ).getBoolean(
                context.getString(R.string.settings_metric_units_key),
                false
        )
        val format = if (useMetricUnits) R.string.track_distance_km else R.string.track_distance_mi
        return context.getString(format, track.distance)
    }

    fun getDuration(context: Context, track: Track): String {
        val trackStart = track.start
                ?: throw IllegalArgumentException("Track doesn't have a start")
        val trackEnd = track.end
                ?: throw IllegalArgumentException("Track doesn't have an end")

        return Duration.between(trackStart, trackEnd).let {
            val resources = context.resources
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

    fun setImage(context: Context, track: Track, imageView: ImageView) {
        val inDayMode = DayNightThemeUtils(context).inDayMode()
        val image = track.imageLocal ?: track.imageRemote
        val errorDrawable = context.getDrawable(R.drawable.ic_terrain_black_24dp).apply {
            val tintColor = if (inDayMode)
                R.color.trackImageForegroundDay
            else
                R.color.trackImageForegroundNight
            setTint(ContextCompat.getColor(context, tintColor))
        }
        val imageBackgroundColor = if (inDayMode)
            R.color.trackImageBackgroundDay
        else
            R.color.trackImageBackgroundNight
        imageView.setBackgroundResource(imageBackgroundColor)

        GlideApp.with(context)
                .load(image)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(errorDrawable)
                .centerCrop()
                .into(imageView)
    }
}