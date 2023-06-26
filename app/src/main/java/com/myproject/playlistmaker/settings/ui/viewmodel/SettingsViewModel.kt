package com.myproject.playlistmaker.settings.ui.viewmodel

import android.content.res.Configuration
import androidx.lifecycle.*
import com.myproject.playlistmaker.App

class SettingsViewModel(private val application: App) : AndroidViewModel(application) {

    private val settingsInteractor = application.settingsInteractor

    fun switchTheme(isChecked: Boolean) {
        settingsInteractor.switch(isChecked)
    }

    fun isDarkOn(): Boolean {
        return settingsInteractor.isDarkOn()
    }

    fun getCurrentTheme(): Int {
        return if (isDarkOn()) {
            Configuration.UI_MODE_NIGHT_YES
        } else {
            Configuration.UI_MODE_NIGHT_NO
        }
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
