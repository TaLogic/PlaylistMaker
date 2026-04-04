package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Error(val type: ErrorType) : SearchState

    object Empty : SearchState

    data class Content(val tracks: List<Track>) : SearchState

    data class History(val tracks: List<Track>) : SearchState
}

sealed interface ErrorType {
    object Network : ErrorType
    object Server : ErrorType
}