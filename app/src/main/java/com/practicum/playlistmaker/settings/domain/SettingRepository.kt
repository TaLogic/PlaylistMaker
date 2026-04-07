package com.practicum.playlistmaker.settings.domain

interface SettingRepository {
    fun updateThemeSetting(isDark: Boolean)
    fun isDarkThemeEnabled(): Boolean
}