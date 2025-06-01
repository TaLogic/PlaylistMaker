package com.practicum.playlistmaker.search

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    fun saveHistoryTracks(tracks: ArrayList<Track>) {
        val savedTracks = createJsonFromTrack(tracks)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, savedTracks)
            .apply()
    }

    fun getHistoryTracks(): ArrayList<Track> {
        val tracks = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return arrayListOf()
        return createTrackListFromJson(tracks).toCollection(ArrayList())
    }

    fun clearSearchHistory() {
        sharedPrefs.edit()
            .clear()
            .apply()
    }

    private fun createJsonFromTrack(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}