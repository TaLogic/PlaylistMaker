package com.practicum.playlistmaker.domain.player

interface AudioPlayer {
    fun preparePlayer(urlTrack: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun currentPosition(): Int

    fun setPlayerStateListener(listener: PlayerStateListener)
}