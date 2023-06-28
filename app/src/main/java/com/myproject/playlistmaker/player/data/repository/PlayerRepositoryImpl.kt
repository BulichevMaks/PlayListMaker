package com.myproject.playlistmaker.player.data.repository

import com.myproject.playlistmaker.player.data.datasource.PlayerSharedPrefDataSourceImpl
import com.myproject.playlistmaker.player.domain.api.PlayerRepository
import com.myproject.playlistmaker.player.domain.model.Track


class PlayerRepositoryImpl(private val tracksSharedPrefStorage: PlayerSharedPrefDataSourceImpl): PlayerRepository {
    override fun getTrackFromSharedPref(): Track {
        return PlayerDataMapper.transformTrackDataSourceToDomainModels(tracksSharedPrefStorage.get())
    }

}