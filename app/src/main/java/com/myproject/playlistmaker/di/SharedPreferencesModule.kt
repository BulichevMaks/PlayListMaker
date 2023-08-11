package com.myproject.playlistmaker.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val PREFERENCES = "preferences"

val sharedPreferencesModule = module {
    single {
        androidContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }
}