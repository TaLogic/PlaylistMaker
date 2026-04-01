package com.practicum.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class SharedPreferencesClient<T>(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val dataKey: String,
    private val type: Type
) :
    StorageClient<T> {

    override fun storeData(data: T) {
        sharedPrefs.edit()
            .putString(dataKey,gson.toJson(data, type))
            .apply()
    }

    override fun getData(): T? {
        val dataJson = sharedPrefs.getString(dataKey, null) ?: return null
        return gson.fromJson(dataJson, type)
    }

    override fun clear() {
        sharedPrefs.edit()
            .remove(dataKey)
            .apply()
    }
}