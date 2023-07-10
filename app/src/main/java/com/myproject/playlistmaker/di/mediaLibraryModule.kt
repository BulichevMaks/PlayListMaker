package com.myproject.playlistmaker.di

import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.medialibrary.viewmodel.FavoriteTracksViewModel
import com.myproject.playlistmaker.medialibrary.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavoriteTracksViewModel(androidContext() as App)
    }

    viewModel {
        PlaylistsViewModel(androidContext() as App)
    }
}