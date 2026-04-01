package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun updateThemeSetting(isDark: Boolean)
    fun isDarkThemeEnabled(): Boolean
}