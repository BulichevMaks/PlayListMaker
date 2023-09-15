package com.myproject.playlistmaker.player.data.repository

import com.myproject.playlistmaker.db.room.AppDatabase
import com.myproject.playlistmaker.player.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.player.domain.api.PlayerRepository
import com.myproject.playlistmaker.search.domain.model.Track


class PlayerRepositoryImpl(
    private val tracksSharedPrefStorage: TracksSharedPrefStorage,
    private val appDatabase: AppDatabase,
): PlayerRepository {
    override fun getTrackFromSharedPref(): Track {
        return PlayerDataMapper.transformTrackDataSourceToDomainModels(tracksSharedPrefStorage.get())
    }
    override suspend fun saveTrackToDB() {
        appDatabase.trackDao().insertTrack(PlayerDataMapper.transformTrackToDBaseModel(tracksSharedPrefStorage.get()))
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().getTrack(trackId) != null
    }

    override suspend fun deleteById(trackId: Long){
        appDatabase.trackDao().deleteById(trackId)
    }

}