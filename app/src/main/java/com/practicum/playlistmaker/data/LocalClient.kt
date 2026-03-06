package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto

interface LocalClient {
    fun saveHistoryTracks(tracksDto: List<TrackDto>)
    fun getHistoryTracks(): List<TrackDto>
    fun clearSearchHistory()
}