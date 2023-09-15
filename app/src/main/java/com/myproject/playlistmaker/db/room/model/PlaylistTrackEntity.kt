package com.myproject.playlistmaker.db.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String?,
    val artworkUrl100: String,
    val previewUrl: String?,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
)