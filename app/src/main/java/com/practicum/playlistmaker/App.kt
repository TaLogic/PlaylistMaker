package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.interactor.SettingInteractor

class App : Application() {
    lateinit var settingInteractor: SettingInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        settingInteractor = Creator.provideSettingInteractor()

        applyTheme( settingInteractor.darkTheme)
    }

    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

        fun switchTheme(isDark: Boolean) {
        settingInteractor.darkTheme = isDark
        applyTheme(isDark)
    }

    companion object {
        const val USER_PREFERENCES = "user_preferences"
        const val SEARCH_PREFERENCES = "search_history_preferences"
    }
}