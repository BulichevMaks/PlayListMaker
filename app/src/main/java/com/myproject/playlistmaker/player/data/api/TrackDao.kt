package com.myproject.playlistmaker.player.data.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myproject.playlistmaker.db.room.model.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    suspend fun getTrack(trackId: Long): TrackEntity?

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Long): Int

}