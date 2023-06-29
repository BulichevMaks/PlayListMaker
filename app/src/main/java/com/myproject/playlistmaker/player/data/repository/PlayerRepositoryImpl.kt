package com.myproject.playlistmaker.player.data.repository

import com.myproject.playlistmaker.player.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.player.domain.api.PlayerRepository
import com.myproject.playlistmaker.player.domain.model.Track


class PlayerRepositoryImpl(private val tracksSharedPrefStorage: TracksSharedPrefStorage): PlayerRepository {
    override fun getTrackFromSharedPref(): Track {
        return PlayerDataMapper.transformTrackDataSourceToDomainModels(tracksSharedPrefStorage.get())
    }

}