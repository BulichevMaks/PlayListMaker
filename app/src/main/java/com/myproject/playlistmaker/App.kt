package com.myproject.playlistmaker

import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.myproject.playlistmaker.di.mediaLibraryModule
import com.myproject.playlistmaker.di.playerModule
import com.myproject.playlistmaker.di.searchModule
import com.myproject.playlistmaker.di.settingsModule
import com.myproject.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor

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
        settingsInteractor.useCurrentTheme()

        val configuration = resources.configuration
        var currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                val newNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode != newNightMode) {
                    currentNightMode = newNightMode
                    switchTheme(currentNightMode)
                }
            }

            override fun onLowMemory() {}
        })

    }

    fun switchTheme(darkThemeEnabled: Int) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}
