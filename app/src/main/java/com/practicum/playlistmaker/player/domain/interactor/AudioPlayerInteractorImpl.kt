package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.player.domain.repository.AudioPlayer
import com.practicum.playlistmaker.player.domain.PlaybackListener
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.PlayerStateListener

class AudioPlayerInteractorImpl(private val audioPlayer: AudioPlayer) : AudioPlayerInteractor {
    // listener для UI слоя
    private var playbackListener: PlaybackListener? = null

    private var playerState = PlayerState.DEFAULT

    // listener для AudioPlayerImpl
    private val playerStateListener = object : PlayerStateListener {
        override fun onPrepared() {
            // Событие от MediaPlayer: трек готов к воспроизведению
            playbackListener?.onPrepared()
            playerState = PlayerState.PREPARED
        }

        override fun onCompleted() {
            // Событие от MediaPlayer: трек закончился
            playbackListener?.onCompleted() // отправляем событие в UI через playbackListener
            playerState = PlayerState.PREPARED
        }

    }

    // Привязываем listener к data-слою сразу при создании итерактора
    init {
        audioPlayer.setPlayerStateListener(playerStateListener)
    }

    override fun preparePlayer(urlTrack: String?) {
        if (urlTrack != null) {
            audioPlayer.preparePlayer(urlTrack)
        }
    }

    override fun playTrack() {
        audioPlayer.startPlayer()
        playerState = PlayerState.PLAYING
        playbackListener?.onPlay()
    }

    override fun pauseTrack() {
        audioPlayer.pausePlayer()
        playerState = PlayerState.PAUSED
        playbackListener?.onPause()
    }

    override fun releasePlayer() {
        audioPlayer.releasePlayer()
        playerState = PlayerState.DEFAULT
    }

    override fun playbackControl() {
        when(playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                playTrack()
            }
            PlayerState.PLAYING -> {
                pauseTrack()
            }
            else -> {}
        }
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.currentPosition()
    }

    override fun setPlaybackListener(listener: PlaybackListener) {
        playbackListener = listener
    }
}