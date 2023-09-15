package com.myproject.playlistmaker.medialibrary.domain.api


import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

   fun getTracks(): Flow<List<Track>>

   fun saveTrackToSharedPref(track: Track)

}