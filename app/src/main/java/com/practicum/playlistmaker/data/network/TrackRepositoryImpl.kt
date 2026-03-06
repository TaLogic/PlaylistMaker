package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.ITunesRequest
import com.practicum.playlistmaker.data.dto.ITunesNetworkResponse
import com.practicum.playlistmaker.domain.TracksResult
import com.practicum.playlistmaker.domain.repository.TracksRepository
import com.practicum.playlistmaker.domain.model.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): TracksResult {
        val response = networkClient.doRequest(ITunesRequest(expression))
        when(response.resultCode) {
            200 ->  {
                val tracks = (response as ITunesNetworkResponse).results.map {
                    Track(it.trackId, it.trackName, it.artistName, it.trackTime, it.artworkUrl100, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.previewUrl)
                }
                return if (tracks.isEmpty()) {
                    TracksResult.Empty
                } else {
                    TracksResult.Success(tracks)
                }
            }
            -1 -> return TracksResult.NetworkError
            else -> return TracksResult.ServerError
        }
    }
}