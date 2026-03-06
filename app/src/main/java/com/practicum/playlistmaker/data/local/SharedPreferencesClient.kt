package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.LocalClient
import com.practicum.playlistmaker.data.dto.TrackDto

class SharedPreferencesClient(private val sharedPrefs: SharedPreferences) : LocalClient{

    private val gson = Gson()

    override fun saveHistoryTracks(tracksDto: List<TrackDto>) {
        val savedTracks = createJsonFromTrack(tracksDto)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, savedTracks)
            .apply()
    }

    override fun getHistoryTracks(): List<TrackDto> {
        val tracksDto = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return createTrackListFromJson(tracksDto)
    }

    override fun clearSearchHistory() {
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun createJsonFromTrack(tracksDto: List<TrackDto>): String {
        return gson.toJson(tracksDto)
    }

    private fun createTrackListFromJson(json: String): List<TrackDto> {
        return gson.fromJson(json, Array<TrackDto>::class.java).toList()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}