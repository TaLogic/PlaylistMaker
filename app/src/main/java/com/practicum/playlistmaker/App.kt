package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {
    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        settingsInteractor = Creator.provideSettingInteractor()
        val isDark = settingsInteractor.isDarkThemeEnabled()
        settingsInteractor.updateThemeSetting(isDark)
    }

    companion object {
        const val USER_PREFERENCES = "user_preferences"
        const val SEARCH_PREFERENCES = "search_history_preferences"
    }
}