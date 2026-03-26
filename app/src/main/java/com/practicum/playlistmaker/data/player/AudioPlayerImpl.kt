package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.player.AudioPlayer
import com.practicum.playlistmaker.domain.player.PlayerStateListener


class AudioPlayerImpl : AudioPlayer {
    private val mediaPlayer = MediaPlayer()
    private var playerStateListener: PlayerStateListener ?= null

    override fun preparePlayer(urlTrack: String) {
        mediaPlayer.setDataSource(urlTrack)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateListener?.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerStateListener?.onCompleted()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setPlayerStateListener(listener: PlayerStateListener) {
        playerStateListener = listener
    }
}