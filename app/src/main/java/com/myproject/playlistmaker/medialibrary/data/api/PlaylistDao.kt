package com.myproject.playlistmaker.medialibrary.data.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myproject.playlistmaker.db.room.model.PlaylistEntity
import com.myproject.playlistmaker.db.room.model.PlaylistTrackEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(track: PlaylistTrackEntity)

}