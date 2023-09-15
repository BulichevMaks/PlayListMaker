package com.myproject.playlistmaker.medialibrary.data.repository

import com.myproject.playlistmaker.db.room.AppDatabase
import com.myproject.playlistmaker.db.room.model.TrackEntity
import com.myproject.playlistmaker.medialibrary.data.api.FavoriteSharedPrefDataSource
import com.myproject.playlistmaker.medialibrary.data.converters.TrackDbConvertor
import com.myproject.playlistmaker.medialibrary.data.model.TrackDataSource
import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
    private val tracksSharedPrefStorage: FavoriteSharedPrefDataSource,
) : FavoriteTracksRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun saveTrackToSharedPref(track: Track) {
        tracksSharedPrefStorage.save(convertFromTrackToTrackDataSource(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromTrackToTrackDataSource(track: Track): TrackDataSource {
        return trackDbConvertor.map(track)
    }



}