package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track

class TracksAdapter(val clickListener: ClickListener) : RecyclerView.Adapter<TracksViewHolder>() {

    private var trackList: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder =
        TracksViewHolder.from(parent)

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(track)
        }
    }

    fun setTracks(tracks: List<Track>) {
        trackList = tracks
        notifyDataSetChanged()
    }

    fun interface ClickListener {
        fun onTrackClick(trackDto: Track)
    }
}