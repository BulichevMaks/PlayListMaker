package com.myproject.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.*
import com.myproject.playlistmaker.App

class SettingsViewModel(application: App) : AndroidViewModel(application) {

    private val settingsInteractor = application.settingsInteractor

    fun switchTheme(isChecked: Boolean) {
        settingsInteractor.switch(isChecked)
    }

    fun isDarkOn(): Boolean {
        return settingsInteractor.isDarkOn()
    }

    companion object {
        fun getViewModelFactory(application: App): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(
                        application = application
                    ) as T
                }
            }
    }
}
