package com.practicum.playlistmaker.domain.player

interface AudioPlayerInteractor {
    fun preparePlayer(urlTrack: String?)
    fun playTrack()
    fun pauseTrack()
    fun releasePlayer()
    fun playbackControl()
    fun getCurrentPosition(): Int

    fun setPlaybackListener(listener: PlaybackListener)
}