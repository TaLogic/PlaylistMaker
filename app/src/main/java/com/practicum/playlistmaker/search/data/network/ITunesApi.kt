package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.network.dto.ITunesNetworkResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("/search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song",
    ): Call<ITunesNetworkResponse>

}