package com.myproject.playlistmaker.search.data

import com.myproject.playlistmaker.search.data.model.TrackDataSource
import com.myproject.playlistmaker.search.domain.model.Track
import kotlin.collections.ArrayList

object Mapper {


    fun transformDomainModelsToTracksDataSource(tracks: ArrayList<Track>): ArrayList<TrackDataSource> {
        val transformedTracks: ArrayList<TrackDataSource> = ArrayList()

        for (track in tracks) {
            val transformedTrack = TrackDataSource(
                id = track.id,
                trackId = track.trackId,
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
            transformedTracks.add(transformedTrack)
        }
        return transformedTracks
    }


    fun transformTrackDataSourceToDomainModels(track: Track): TrackDataSource {
        return TrackDataSource(
            id = track.id,
            trackId = track.trackId,
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
    fun transformTrackDataSourceToDomainModels(track: TrackDataSource): Track {
        return Track(
            id = track.id,
            trackId = track.trackId,
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