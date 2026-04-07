package com.practicum.playlistmaker.search.data.network

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.network.dto.ITunesRequest
import com.practicum.playlistmaker.search.data.network.dto.NetworkResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val iTunesBaseUrl: String, private val gson: Gson) :
    NetworkClient {

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