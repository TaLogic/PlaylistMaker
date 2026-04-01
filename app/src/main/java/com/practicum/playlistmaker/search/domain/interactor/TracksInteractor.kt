package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.TracksResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: TracksResult)
    }
}