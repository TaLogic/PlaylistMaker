package com.practicum.playlistmaker.player.domain

interface PlayerStateListener {
    fun onPrepared()
    fun onCompleted()
}