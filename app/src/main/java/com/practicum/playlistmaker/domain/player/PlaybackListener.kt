package com.practicum.playlistmaker.domain.player

interface PlaybackListener {
    fun onCompleted()
    fun onPlay()
    fun onPause()
}