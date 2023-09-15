package com.myproject.playlistmaker.medialibrary.data.converters

import android.net.Uri
import com.myproject.playlistmaker.db.room.model.PlaylistEntity
import com.myproject.playlistmaker.db.room.model.TrackEntity
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.search.domain.model.Track


class PlayListDbConvertor {

    fun toPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            image = playlist.image?.toString(),
            name = playlist.name,
            description = playlist.description,
            tracks = playlist.tracks.map { track -> track.toTrackEntity() }.toList()
        )
    }

    fun toPlaylist(playlists: List<PlaylistEntity>): List<Playlist> {

        val transformedPlaylists = mutableListOf<Playlist>()

        for (playlist in playlists) {
            val transformedTrack = Playlist(
                id = playlist.id,
                image = playlist.image?.let { Uri.parse(it) },
                name = playlist.name,
                description = playlist.description,
                tracks = playlist.tracks.map { track -> track.toTrack() }.toMutableList()
            )
            transformedPlaylists.add(transformedTrack)
        }
        return transformedPlaylists
    }

    fun Track.toTrackEntity(): TrackEntity {
        return TrackEntity(
            id,
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            previewUrl,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
        )
    }

    fun TrackEntity.toTrack() : Track {
        return Track(
            id,
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            previewUrl,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
        )
    }
}