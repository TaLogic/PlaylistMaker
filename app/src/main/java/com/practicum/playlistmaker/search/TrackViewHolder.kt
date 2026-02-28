package com.practicum.playlistmaker.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)
    private val trackImage = itemView.findViewById<ImageView>(R.id.track_image)

    fun bind(track: Track) {
        artistName.text = "" // Сброс для правильной ширины после повторного использования ViewHolder (исправляет баг с layout_weight)

        trackName.text = track.trackName ?: itemView.context.getString(R.string.unknown_track)
        artistName.text = track.artistName ?: itemView.context.getString(R.string.unknown_artist)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime) ?: itemView.context.getString(R.string.unknown_time)

        //dp to px
        val cornerRadius = itemView.context.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(trackImage)
    }
}
