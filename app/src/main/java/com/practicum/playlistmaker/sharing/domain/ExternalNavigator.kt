package com.practicum.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink()
    fun openEmail()
    fun openLink()
}