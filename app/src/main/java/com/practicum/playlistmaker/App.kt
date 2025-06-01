package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    private lateinit var userPrefs: SharedPreferences
    private lateinit var searchPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        userPrefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        darkTheme = userPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)

        searchPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        userPrefs.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getSearchPrefs(): SharedPreferences = searchPrefs

    companion object {
        const val USER_PREFERENCES = "user_preferences"
        const val THEME_KEY = "theme_key"
        const val SEARCH_PREFERENCES = "search_history_preferences"
    }
}