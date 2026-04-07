package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.network.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse
}