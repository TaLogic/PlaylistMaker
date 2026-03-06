package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.TracksResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TracksResult)
    }
}