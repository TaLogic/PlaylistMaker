package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.player.data.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.repository.AudioPlayer
import com.practicum.playlistmaker.search.data.local.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.local.SharedPreferencesClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.SettingRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    // Состояние
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    // Конфигурация
    private const val ITUNES_BASE_URL = "https://itunes.apple.com"

    private fun getGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()
    }

    // Tracks
    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(ITUNES_BASE_URL, getGson()))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    // SearchHistory
    private const val SEARCH_HISTORY_KEY = "search_history_key"

    private fun getSearchPreferences(): SharedPreferences {
        return application.getSharedPreferences(
            App.SEARCH_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SharedPreferencesClient<List<Track>>(
                getSearchPreferences(),
                getGson(), SEARCH_HISTORY_KEY, object : TypeToken<List<Track>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    // Settings
    private fun getUserPreferences(): SharedPreferences {
        return application.getSharedPreferences(
            App.USER_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    private fun getSettingRepository(): SettingRepository {
        return SettingRepositoryImpl(getUserPreferences())
    }

    fun provideSettingInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingRepository())
    }

    // Player
    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl(getMediaPlayer(), application)
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayer())
    }

    // Sharing

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator())
    }

}