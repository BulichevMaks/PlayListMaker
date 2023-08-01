package com.myproject.playlistmaker

import android.app.Application
import com.myproject.playlistmaker.di.mediaLibraryModule
import com.myproject.playlistmaker.di.playerModule
import com.myproject.playlistmaker.di.searchModule
import com.myproject.playlistmaker.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

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
    }

}
