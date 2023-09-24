package com.myproject.playlistmaker.medialibrary.data.converters

import android.util.Log
import com.myproject.playlistmaker.db.room.model.PlaylistTrackEntity
import com.myproject.playlistmaker.search.domain.model.Track

class PlaylistTrackConverter {

    fun map(track: Track, id: Int): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            id = 0,
            track.trackId,
            playlistId = id,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
        )
    }

    fun map(track: PlaylistTrackEntity): Track {
        return Track(
            track.id,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
        )
    }
}