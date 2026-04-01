package com.practicum.playlistmaker.player.domain

interface PlaybackListener {
    fun onCompleted()
    fun onPlay()
    fun onPause()
}