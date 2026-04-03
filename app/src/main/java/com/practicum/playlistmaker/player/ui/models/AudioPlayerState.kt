package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.player.domain.PlayerState

data class AudioPlayerState(val playerState: PlayerState, val currentTime: String)