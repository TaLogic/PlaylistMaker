package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.player.domain.PlaybackListener

interface AudioPlayerInteractor {
    fun preparePlayer(urlTrack: String?)
    fun playTrack()
    fun pauseTrack()
    fun releasePlayer()
    fun playbackControl()
    fun getCurrentPosition(): Int

    fun setPlaybackListener(listener: PlaybackListener)
}