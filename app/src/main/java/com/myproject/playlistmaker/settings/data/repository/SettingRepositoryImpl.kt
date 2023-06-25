package com.myproject.playlistmaker.settings.data.repository

import androidx.appcompat.app.AppCompatDelegate
import com.myproject.playlistmaker.settings.data.api.SettingsStorage
import com.myproject.playlistmaker.settings.domain.SettingRepository

class SettingRepositoryImpl(private val storage: SettingsStorage): SettingRepository{
    override fun switch(darkThemeEnabled: Boolean) {
        storage.switch(darkThemeEnabled)
        useCurrentTheme()
    }

    override fun isDarkOn(): Boolean {
        return storage.isDarkOn()
    }

    override fun useCurrentTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkOn()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
