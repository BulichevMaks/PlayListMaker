package com.myproject.playlistmaker.db.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val image: String?,
    val name: String,
    val description: String?,
    val tracks: List<TrackEntity> = listOf()
)
