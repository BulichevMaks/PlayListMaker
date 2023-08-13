package com.myproject.playlistmaker.player.domain.api

import com.myproject.playlistmaker.player.data.repository.PlayerDataMapper
import com.myproject.playlistmaker.player.domain.model.Track

interface PlayerInteractor {
    fun getTrack(): Track
    fun preparePlayer()
    fun pausePlayer()
    fun onDestroy()
    fun playerControl()
    fun isPlayerPlaying(): Boolean
    fun getTiming(): String
    fun playerCompletion(onComplete: () -> Unit)


    suspend fun saveTrackToDB()
    suspend fun isTrackFavorite(trackId: Long): Boolean
    suspend fun deleteById(trackId: Long)
}