package com.practicum.playlistmaker.search.domain.search_history

import com.practicum.playlistmaker.search.domain.Track

interface SearchHistoryInteractor {

    fun saveToHistory(track: Track)

    fun getHistoryTracks(): List<Track>

    fun clearSearchHistory()
}