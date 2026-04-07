package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.player.domain.PlayerStateListener

interface AudioPlayer {
    fun preparePlayer(urlTrack: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun currentPosition(): Int

    fun setPlayerStateListener(listener: PlayerStateListener)
}