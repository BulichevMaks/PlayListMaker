package com.myproject.playlistmaker.medialibrary.data.converters

import com.myproject.playlistmaker.db.room.model.TrackEntity
import com.myproject.playlistmaker.medialibrary.data.model.TrackDataSource
import com.myproject.playlistmaker.medialibrary.domain.model.Track

class TrackDbConvertor {

    fun map(track: Track): TrackDataSource {
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

    fun map(track: TrackEntity): Track {
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