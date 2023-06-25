package com.myproject.playlistmaker

import android.app.Application
import com.myproject.playlistmaker.settings.di.Creator
import com.myproject.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        settingsInteractor.useCurrentTheme()
        instance = this@App
    }

    companion object {
        private lateinit var instance: App
        fun getInstance(): App {
            return instance
        }
    }
}