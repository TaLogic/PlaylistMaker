package com.practicum.playlistmaker.player.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.practicum.playlistmaker.player.domain.repository.AudioPlayer
import com.practicum.playlistmaker.player.domain.PlayerStateListener

class AudioPlayerImpl(private val mediaPlayer: MediaPlayer, private val context: Context) : AudioPlayer {
    private var playerStateListener: PlayerStateListener?= null

    override fun preparePlayer(urlTrack: String) {
        mediaPlayer.setDataSource(context, Uri.parse(urlTrack))
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