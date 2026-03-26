package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.repository.SettingRepository

class SettingInteractorImpl(private val repository: SettingRepository): SettingInteractor {
    override var darkTheme: Boolean
        get() = repository.darkTheme
        set(value) {
            repository.darkTheme = value
        }
}