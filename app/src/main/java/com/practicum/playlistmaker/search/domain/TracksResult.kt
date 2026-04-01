package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.Track

sealed class TracksResult {

    data class Success(val tracks: List<Track>): TracksResult()

    object Empty : TracksResult()

    object NetworkError : TracksResult()

    object ServerError : TracksResult()

    object EmptyQuery : TracksResult()

}