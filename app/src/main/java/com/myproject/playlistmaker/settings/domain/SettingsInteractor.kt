package com.myproject.playlistmaker.settings.domain

interface SettingsInteractor {
    fun switch(isDarkOn: Boolean)
    fun isDarkOn(): Boolean
    fun useCurrentTheme()
}