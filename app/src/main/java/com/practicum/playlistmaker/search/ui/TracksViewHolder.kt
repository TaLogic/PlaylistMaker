package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.formatDuration
import com.practicum.playlistmaker.search.domain.Track

class TracksViewHolder(private val binding: ItemTrackBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.apply {
            artistName.text =
                "" // Сброс для правильной ширины после повторного использования ViewHolder (исправляет баг с layout_weight)

            trackName.text = track.trackName ?: itemView.context.getString(R.string.unknown_track)
            artistName.text =
                track.artistName ?: itemView.context.getString(R.string.unknown_artist)
            trackTime.text = formatDuration(track.trackTime)
//            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime) ?: itemView.context.getString(
//            R.string.unknown_time)
        }

        //dp to px
        val cornerRadius = itemView.context.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)

        Glide.with(binding.root)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(binding.trackImage)
    }

    companion object {
        fun from(parent: ViewGroup): TracksViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTrackBinding.inflate(inflater, parent, false)

            return TracksViewHolder(binding)
        }
    }
}