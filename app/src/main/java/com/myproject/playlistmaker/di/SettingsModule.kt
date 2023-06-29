package com.myproject.playlistmaker.di

import android.content.Context
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.settings.data.api.SettingsStorage
import com.myproject.playlistmaker.settings.data.repository.SettingRepositoryImpl
import com.myproject.playlistmaker.settings.data.storage.SettingsStorageImpl
import com.myproject.playlistmaker.settings.domain.SettingRepository
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import com.myproject.playlistmaker.settings.domain.SettingsInteractorImpl
import com.myproject.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val THEME_PREFERENCES = "current_theme"

val settingsModule = module {

    single {
        androidContext()
            .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }

    factory <SettingRepository> {
        SettingRepositoryImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    viewModel {
        SettingsViewModel(androidContext() as App)
    }

}