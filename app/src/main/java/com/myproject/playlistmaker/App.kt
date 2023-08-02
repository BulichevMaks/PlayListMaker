package com.myproject.playlistmaker

import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import com.myproject.playlistmaker.di.mediaLibraryModule
import com.myproject.playlistmaker.di.playerModule
import com.myproject.playlistmaker.di.searchModule
import com.myproject.playlistmaker.di.settingsModule
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import com.myproject.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor
    lateinit var vm: SettingsViewModel

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                mediaLibraryModule,
                playerModule,
                searchModule,
                settingsModule
            )
        }

        settingsInteractor = getKoin().get()
        vm = getKoin().get()
        settingsInteractor.useCurrentTheme()

        val configuration = resources.configuration
        var currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                val newNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode != newNightMode) {
                    currentNightMode = newNightMode
                    vm.switchTheme(newNightMode == Configuration.UI_MODE_NIGHT_YES)//на этой строке ошибка: Unresolved reference: vm
                }
            }

            override fun onLowMemory() {}
        })

    }
    fun getCurrentNightMode(): Int {
        val configuration = resources.configuration
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    }

}
