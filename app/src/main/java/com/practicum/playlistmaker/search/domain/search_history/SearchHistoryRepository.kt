package com.practicum.playlistmaker.search.domain.search_history

import com.practicum.playlistmaker.search.domain.Track

interface SearchHistoryRepository {

    fun saveHistoryTracks(tracks: List<Track>)

    fun getHistoryTracks(): List<Track>

    fun clearSearchHistory()
}