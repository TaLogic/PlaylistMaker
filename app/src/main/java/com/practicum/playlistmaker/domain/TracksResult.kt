package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.model.Track

sealed class TracksResult {

    data class Success(val tracks: List<Track>): TracksResult()

    object Empty : TracksResult()

    object NetworkError : TracksResult()

    object ServerError : TracksResult()

    object EmptyQuery : TracksResult()

}