package com.practicum.playlistmaker.domain.player

interface PlayerStateListener {
    fun onPrepared()
    fun onCompleted()
}