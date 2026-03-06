package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun saveHistoryTracks(tracks: List<Track>)
    fun getHistoryTracks(): List<Track>
    fun clearSearchHistory()
}