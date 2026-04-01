package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingRepository

class SettingRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingRepository {

    override fun updateThemeSetting(isDark: Boolean) {
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, isDark)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }


    companion object {
        const val THEME_KEY = "theme_key"
    }
}