package com.practicum.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.practicum.playlistmaker.App.Companion.SEARCH_PREFERENCES
import com.practicum.playlistmaker.App.Companion.USER_PREFERENCES
import com.practicum.playlistmaker.data.local.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.local.SettingRepositoryImpl
import com.practicum.playlistmaker.data.local.SharedPreferencesClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.data.player.AudioPlayerImpl
import com.practicum.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.SettingInteractor
import com.practicum.playlistmaker.domain.interactor.SettingInteractorImpl
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.repository.TracksRepository
import com.practicum.playlistmaker.domain.interactor.TracksInteractorImpl
import com.practicum.playlistmaker.domain.player.AudioPlayer
import com.practicum.playlistmaker.domain.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.player.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.SettingRepository

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchPreferences(): SharedPreferences {
        return application.getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
    }
    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SharedPreferencesClient(getSearchPreferences()))
    }
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getUserPreferences(): SharedPreferences {
        return application.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
    }
    private fun getSettingRepository(): SettingRepository {
        return SettingRepositoryImpl(getUserPreferences())
    }
    fun provideSettingInteractor(): SettingInteractor {
        return SettingInteractorImpl(getSettingRepository())
    }

    private fun getAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }
    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayer())
    }

}