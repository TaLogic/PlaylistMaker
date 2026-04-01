package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.search.domain.TracksResult

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