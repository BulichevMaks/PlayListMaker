package com.myproject.playlistmaker.settings.ui.viewmodel

import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin


class SettingsViewModel(
    application: App
) : AndroidViewModel(application) {

    private val settingsInteractor: SettingsInteractor = application.getKoin().get()

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

}
