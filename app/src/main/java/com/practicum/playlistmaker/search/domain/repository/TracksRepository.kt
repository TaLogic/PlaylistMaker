package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.TracksResult

interface TracksRepository {
    fun searchTracks(expression: String): TracksResult
}