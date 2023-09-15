package com.myproject.playlistmaker.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myproject.playlistmaker.db.room.model.PlaylistEntity
import com.myproject.playlistmaker.db.room.model.PlaylistTrackEntity
import com.myproject.playlistmaker.db.room.model.TrackEntity

@Database(version = 1, entities = [TrackEntity::class,
    PlaylistEntity::class, PlaylistTrackEntity::class])
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

}