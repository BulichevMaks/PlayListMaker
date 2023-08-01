package com.myproject.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import com.myproject.playlistmaker.settings.data.api.SettingsStorage

class SettingsStorageImpl(private val sharedPreferences: SharedPreferences): SettingsStorage {
    override fun switch(darkThemeEnabled: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(IS_DARK_ON, darkThemeEnabled)
            .apply()
    }

    override fun isDarkOn(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_ON, false)
    }

    override fun isValueExists(): Boolean {
        return sharedPreferences.contains("is_dark_on")
    }

    companion object{
        private const val IS_DARK_ON = "is_dark_on"
    }
}
