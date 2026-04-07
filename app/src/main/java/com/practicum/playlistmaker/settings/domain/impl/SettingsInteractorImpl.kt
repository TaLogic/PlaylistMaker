package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingRepository

class SettingsInteractorImpl(private val repository: SettingRepository): SettingsInteractor {

    override fun updateThemeSetting(isDark: Boolean) {
        repository.updateThemeSetting(isDark)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }
}