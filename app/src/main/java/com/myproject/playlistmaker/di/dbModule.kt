package com.myproject.playlistmaker.di

import androidx.room.Room
import com.myproject.playlistmaker.db.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}