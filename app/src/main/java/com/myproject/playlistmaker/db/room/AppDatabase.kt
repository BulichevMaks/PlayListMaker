package com.myproject.playlistmaker.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myproject.playlistmaker.db.room.model.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
}