package com.myproject.playlistmaker.settings.domain

class SettingsInteractorImpl(private val settingsRepository: SettingRepository): SettingsInteractor {
    override fun switch(isDarkOn: Boolean) {
        settingsRepository.switch(isDarkOn)
    }
    override fun isDarkOn(): Boolean {
        return settingsRepository.isDarkOn()
    }
    override fun useCurrentTheme() {
        settingsRepository.useCurrentTheme()
    }
}
