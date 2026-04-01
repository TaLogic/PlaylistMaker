package com.practicum.playlistmaker.search.data.local

import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.Track

class SearchHistoryRepositoryImpl
    (private val storageClient: StorageClient<List<Track>>) : SearchHistoryRepository {
    override fun saveHistoryTracks(tracks: List<Track>) {
        storageClient.storeData(tracks)
    }

    override fun getHistoryTracks(): List<Track> {
        val historyTracks = storageClient.getData() ?: listOf()
        return historyTracks
    }

    override fun clearSearchHistory() {
        storageClient.clear()
    }

}