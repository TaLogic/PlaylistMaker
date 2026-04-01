package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.formatDuration
import com.practicum.playlistmaker.player.domain.PlaybackListener
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor

class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val urlTrack: String?
) : ViewModel() {

    companion object {
        fun getFactory(
            audioPlayerInteractor: AudioPlayerInteractor,
            urlTrack: String?
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    AudioPlayerViewModel(audioPlayerInteractor, urlTrack)
                }
            }

        private const val TIMER_UPDATE_DELAY = 300L
    }

    init {
        audioPlayerInteractor.preparePlayer(urlTrack)
        initPlaybackListener()
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val timerLiveData = MutableLiveData<String>("00:00")
    fun observeTimer(): LiveData<String> = timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    fun onPlayClicked() {
        audioPlayerInteractor.playbackControl()
    }

    fun onPause() {
        audioPlayerInteractor.pauseTrack()
    }

    private fun initPlaybackListener() {
        audioPlayerInteractor.setPlaybackListener(object : PlaybackListener {
            override fun onCompleted() {
                playerStateLiveData.postValue(PlayerState.PREPARED)
                resetTimer()
            }

            override fun onPlay() {
                updateTimer()
                playerStateLiveData.postValue(PlayerState.PLAYING)
            }

            override fun onPause() {
                handler.removeCallbacks(updateTimerRunnable)
                playerStateLiveData.postValue(PlayerState.PAUSED)
            }
        })
    }

    private val updateTimerRunnable = Runnable {
        updateTimer()
    }

    private fun updateTimer() {
        timerLiveData.postValue(formatDuration(audioPlayerInteractor.getCurrentPosition().toLong()))
        handler.postDelayed(updateTimerRunnable, TIMER_UPDATE_DELAY)
    }

    private fun resetTimer() {
        handler.removeCallbacks(updateTimerRunnable)
        timerLiveData.postValue("00:00")
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.releasePlayer()
        resetTimer()
    }
}