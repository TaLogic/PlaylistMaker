package com.practicum.playlistmaker.data.local

import com.practicum.playlistmaker.data.LocalClient
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.model.Track

class SearchHistoryRepositoryImpl(private val localClient: LocalClient) : SearchHistoryRepository {
    override fun saveHistoryTracks(tracks: List<Track>) {
        val tracksDto = tracks.map { TrackMapper.mapTrackToDto(it) }
        localClient.saveHistoryTracks(tracksDto)
    }

    override fun getHistoryTracks(): List<Track> {
        return localClient.getHistoryTracks().map { TrackMapper.mapDtoToTrack(it) }

    }

    override fun clearSearchHistory() {
        localClient.clearSearchHistory()
    }

}