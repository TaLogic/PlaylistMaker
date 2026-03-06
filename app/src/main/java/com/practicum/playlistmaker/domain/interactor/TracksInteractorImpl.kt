package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.TracksResult
import com.practicum.playlistmaker.domain.repository.TracksRepository

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer
    ) {
        if (expression.isBlank()) {
            consumer.consume((TracksResult.EmptyQuery))
            return
        }

        val t = Thread {
            consumer.consume(repository.searchTracks(expression))
        }
        t.start()
    }
}