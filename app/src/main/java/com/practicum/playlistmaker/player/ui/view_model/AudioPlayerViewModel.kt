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
import com.practicum.playlistmaker.player.ui.models.AudioPlayerState

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

    private val audioPlayerStateLiveData =
        MutableLiveData<AudioPlayerState>(AudioPlayerState(PlayerState.DEFAULT, "00:00"))
    fun observeState(): LiveData<AudioPlayerState> = audioPlayerStateLiveData

    private val handler = Handler(Looper.getMainLooper())

    fun onPlayClicked() {
        audioPlayerInteractor.playbackControl()
    }

    fun onPause() {
        audioPlayerInteractor.pauseTrack()
    }

    private fun initPlaybackListener() {
        audioPlayerInteractor.setPlaybackListener(object : PlaybackListener {

            override fun onPrepared() {
                audioPlayerStateLiveData.postValue(
                    AudioPlayerState(playerState = PlayerState.PREPARED, "00:00")
                )
            }

            override fun onPlay() {
                audioPlayerStateLiveData.postValue(
                    audioPlayerStateLiveData.value?.copy(playerState = PlayerState.PLAYING)
                )
                updateTimer()
            }

            override fun onPause() {
                handler.removeCallbacks(updateTimerRunnable)
                audioPlayerStateLiveData.postValue(
                    audioPlayerStateLiveData.value?.copy(playerState = PlayerState.PAUSED)
                )
            }

            override fun onCompleted() {
                handler.removeCallbacks(updateTimerRunnable)
                audioPlayerStateLiveData.postValue(
                    AudioPlayerState(
                        playerState = PlayerState.PREPARED,
                        currentTime = "00:00"
                    )
                )
            }
        })
    }

    private val updateTimerRunnable = Runnable {
        updateTimer()
    }

    private fun updateTimer() {
        val time = formatDuration(audioPlayerInteractor.getCurrentPosition().toLong())
        audioPlayerStateLiveData.postValue(AudioPlayerState(PlayerState.PLAYING, time))
        handler.postDelayed(updateTimerRunnable, TIMER_UPDATE_DELAY)
    }

    private fun resetTimer() {
        handler.removeCallbacks(updateTimerRunnable)
        audioPlayerStateLiveData.postValue(
            audioPlayerStateLiveData.value?.copy(currentTime = "00:00")
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.releasePlayer()
        resetTimer()
    }
}