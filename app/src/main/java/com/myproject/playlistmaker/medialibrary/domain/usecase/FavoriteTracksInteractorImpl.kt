package com.myproject.playlistmaker.medialibrary.domain.usecase

import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksInteractor
import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.myproject.playlistmaker.medialibrary.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun getTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getTracks()
    }

    override fun saveTrackToSharedPref(track: Track) {
        favoriteTracksRepository.saveTrackToSharedPref(track)
    }
}