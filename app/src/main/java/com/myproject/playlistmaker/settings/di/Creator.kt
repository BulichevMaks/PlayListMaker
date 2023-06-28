package com.myproject.playlistmaker.settings.di

import android.content.Context
import com.myproject.playlistmaker.settings.data.repository.SettingRepositoryImpl
import com.myproject.playlistmaker.settings.data.storage.SettingsStorageImpl
import com.myproject.playlistmaker.settings.domain.SettingRepository
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import com.myproject.playlistmaker.settings.domain.SettingsInteractorImpl

object Creator {

    private const val THEME_PREFERENCES = "current_theme"

    private fun getThemeSwitchRepository(context: Context): SettingRepository {
        return SettingRepositoryImpl(
            SettingsStorageImpl(context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE))
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getThemeSwitchRepository(context))
    }
}