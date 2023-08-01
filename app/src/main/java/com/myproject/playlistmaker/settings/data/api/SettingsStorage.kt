package com.myproject.playlistmaker.settings.data.api

interface SettingsStorage {
    fun switch(darkThemeEnabled: Boolean)
    fun isDarkOn(): Boolean
    fun isValueExists(): Boolean
}