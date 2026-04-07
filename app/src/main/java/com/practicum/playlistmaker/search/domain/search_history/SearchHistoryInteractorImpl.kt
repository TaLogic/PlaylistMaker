package com.practicum.playlistmaker.search.domain.search_history

import com.practicum.playlistmaker.search.domain.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun saveToHistory(track: Track) {
        val historyTracks = ArrayList(repository.getHistoryTracks())
        when {
            historyTracks.contains(track) -> {
                historyTracks.remove(track)
            }

            historyTracks.size >= 10 -> {
                val lastIndex = historyTracks.lastIndex
                historyTracks.removeAt(lastIndex)

            }
        }

        historyTracks.add(0, track)
        repository.saveHistoryTracks(historyTracks)
    }

    override fun getHistoryTracks(): List<Track> {
        return repository.getHistoryTracks()
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}
