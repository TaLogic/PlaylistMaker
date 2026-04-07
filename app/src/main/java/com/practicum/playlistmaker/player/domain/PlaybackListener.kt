package com.practicum.playlistmaker.player.domain

interface PlaybackListener {
    fun onPrepared()
    fun onPlay()
    fun onPause()
    fun onCompleted()
}