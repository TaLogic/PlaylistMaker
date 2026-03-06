package com.practicum.playlistmaker.data.network

import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.NetworkResponse
import com.practicum.playlistmaker.data.dto.ITunesRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val itunesApiService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is ITunesRequest) {
            return try {

                val resp = itunesApiService.search(dto.expression).execute()

                val body = resp.body() ?: NetworkResponse()

                body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                NetworkResponse().apply { resultCode = -1 }
            }

        } else {
            return NetworkResponse().apply { resultCode = 400 }
        }
    }
}
