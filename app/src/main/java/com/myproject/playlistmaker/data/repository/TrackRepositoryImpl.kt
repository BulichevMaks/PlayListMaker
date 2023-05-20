package com.myproject.playlistmaker.data.repository

import com.myproject.playlistmaker.data.datasource.api.TracksNetworkStorage
import com.myproject.playlistmaker.data.datasource.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.data.datasource.models.TrackDataSource
import com.myproject.playlistmaker.data.datasource.models.TracksResultDataSource
import com.myproject.playlistmaker.domain.models.Track
import com.myproject.playlistmaker.domain.models.TracksResult
import com.myproject.playlistmaker.domain.api.TrackRepository

class TrackRepositoryImpl(
    private val tracksNetworkStorage: TracksNetworkStorage,
    private val tracksSharedPrefStorage: TracksSharedPrefStorage
) : TrackRepository {

    override fun getTracksByName(trackName: String, callback: (TracksResult) -> Unit) {
        tracksNetworkStorage.getTracksByName(trackName) { tracksResultDataSource ->
            // Обработка результата из источника данных
            val result = when (tracksResultDataSource) {
                is TracksResultDataSource.Success -> {
                    // Преобразование треков из источника данных в доменные модели
                    val tracks = transformToDomainModels(tracksResultDataSource.tracks)
                    TracksResult.Success(tracks)
                }
                is TracksResultDataSource.Error -> {
                    TracksResult.Error(tracksResultDataSource.code)
                }
            }
            callback(result) // Вызов callback с результатом
        }
    }

    override fun saveTrackToSharedPref(track: Track) {
        tracksSharedPrefStorage.save(transformTrackToDomainModels(track))
    }

    override fun getTrackFromSharedPref(): Track {
        return transformTrackToDataModels(tracksSharedPrefStorage.get())
    }

    private fun transformToDomainModels(tracks: ArrayList<TrackDataSource>): ArrayList<Track> {
        val transformedTracks: ArrayList<Track> = ArrayList()

        for (trackDataSource in tracks) {
            val transformedTrack = Track(
                id = trackDataSource.id,
                trackName = trackDataSource.trackName,
                artistName = trackDataSource.artistName,
                trackTimeMillis = trackDataSource.trackTimeMillis,
                artworkUrl100 = trackDataSource.artworkUrl100,
                previewUrl = trackDataSource.previewUrl,
                collectionName = trackDataSource.collectionName,
                releaseDate = trackDataSource.releaseDate,
                primaryGenreName = trackDataSource.primaryGenreName,
                country = trackDataSource.country
            )
            transformedTracks.add(transformedTrack)
        }
        return transformedTracks
    }
    private fun transformTrackToDomainModels(track: Track): TrackDataSource {

          return TrackDataSource(
                id = track.id,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                previewUrl = track.previewUrl,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country
            )
    }
    private fun transformTrackToDataModels(track: TrackDataSource): Track {

        return Track(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country
        )
    }
}