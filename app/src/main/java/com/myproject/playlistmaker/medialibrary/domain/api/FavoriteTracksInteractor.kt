package com.myproject.playlistmaker.medialibrary.domain.api

import com.myproject.playlistmaker.medialibrary.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    fun getTracks(): Flow<List<Track>>

    fun saveTrackToSharedPref(track: Track)

}