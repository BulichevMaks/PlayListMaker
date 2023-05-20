package com.myproject.playlistmaker.creator

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.data.datasource.network.TracksNetworkNetworkDataSourceImpl
import com.myproject.playlistmaker.data.datasource.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.data.playerdata.PlayerHandler
import com.myproject.playlistmaker.data.repository.TrackRepositoryImpl

object Creator {
    fun getPlayer(): PlayerHandler {
        return PlayerHandler()
    }

    fun getTrackRepository(): TrackRepositoryImpl {
        val tracksNetworkDataSourceImpl =
            TracksNetworkNetworkDataSourceImpl()

        val tracksSharedPreferences =
            TracksSharedPrefDataSourceImpl(context = App.getInstance().applicationContext)

        return TrackRepositoryImpl(tracksNetworkDataSourceImpl, tracksSharedPreferences)
    }
}