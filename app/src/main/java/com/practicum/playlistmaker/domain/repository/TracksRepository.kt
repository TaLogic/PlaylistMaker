package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.TracksResult

interface TracksRepository {
    fun searchTracks(expression: String): TracksResult
}