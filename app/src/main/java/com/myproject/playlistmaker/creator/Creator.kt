package com.myproject.playlistmaker.creator

import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.data.datasource.network.TracksNetworkDataSourceImpl
import com.myproject.playlistmaker.data.datasource.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.data.playerdata.PlayerHandler
import com.myproject.playlistmaker.data.repository.TrackRepositoryImpl

object Creator {
    fun getPlayer(): PlayerHandler {
        return PlayerHandler()
    }

    fun getTrackRepository(): TrackRepositoryImpl {
        val tracksNetworkDataSourceImpl =
            TracksNetworkDataSourceImpl()

        val tracksSharedPreferences =
            TracksSharedPrefDataSourceImpl(context = App.getInstance().applicationContext)

        return TrackRepositoryImpl(tracksNetworkDataSourceImpl, tracksSharedPreferences)
    }
}