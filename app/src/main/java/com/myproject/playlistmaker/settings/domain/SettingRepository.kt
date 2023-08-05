package com.myproject.playlistmaker.settings.domain

interface SettingRepository {
    fun switch(darkThemeEnabled: Boolean)
    fun isDarkOn(): Boolean
    fun useCurrentTheme()
    fun isValueExists(): Boolean
}