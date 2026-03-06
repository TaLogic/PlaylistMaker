package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun saveHistoryTracks(tracks: List<Track>) {
        repository.saveHistoryTracks(tracks)
    }

    override fun getHistoryTracks(): List<Track> {
        return repository.getHistoryTracks()
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}