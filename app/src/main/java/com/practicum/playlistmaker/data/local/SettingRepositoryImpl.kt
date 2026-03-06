package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.repository.SettingRepository

class SettingRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingRepository {
    override var darkTheme: Boolean
        set(value) {
            sharedPrefs.edit()
                .putBoolean(THEME_KEY, value)
                .apply()
        }
        get() {
            return sharedPrefs.getBoolean(THEME_KEY, false)
        }


    companion object {
        const val THEME_KEY = "theme_key"
    }
}
