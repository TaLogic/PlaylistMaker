package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun saveHistoryTracks(tracks: List<Track>)
    fun getHistoryTracks(): List<Track>
    fun clearSearchHistory()
}