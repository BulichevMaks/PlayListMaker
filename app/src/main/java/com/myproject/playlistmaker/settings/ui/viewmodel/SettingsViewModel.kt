package com.myproject.playlistmaker.settings.ui.viewmodel

import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin


class SettingsViewModel(
    application: App
) : AndroidViewModel(application) {

    private val settingsInteractor: SettingsInteractor = application.getKoin().get()

    private val isCheckedLiveData = MutableLiveData<Boolean>()

    fun observeIsChecked(): LiveData<Boolean> {
        if(settingsInteractor.isValueExists()) {
            isCheckedLiveData.value = isDarkOn()
        } else {
            isCheckedLiveData.value = isDarkThemeEnabled()
        }
        return isCheckedLiveData
    }

    fun switchTheme(isChecked: Boolean) {
        settingsInteractor.switch(isChecked)
    }

    fun isDarkOn(): Boolean {
        return settingsInteractor.isDarkOn()
    }

    private fun isDarkThemeEnabled(): Boolean {
        val configuration = getApplication<App>().resources.configuration
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

}
