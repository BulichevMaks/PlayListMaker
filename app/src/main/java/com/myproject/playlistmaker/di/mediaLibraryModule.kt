package com.myproject.playlistmaker.di

import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.medialibrary.data.api.FavoriteSharedPrefDataSource
import com.myproject.playlistmaker.medialibrary.data.api.InternalStorageClient
import com.myproject.playlistmaker.medialibrary.data.converters.PlayListDbConvertor
import com.myproject.playlistmaker.medialibrary.data.converters.PlaylistTrackConverter
import com.myproject.playlistmaker.medialibrary.data.converters.TrackDbConvertor
import com.myproject.playlistmaker.medialibrary.data.repository.FavoriteTracksRepositoryImpl
import com.myproject.playlistmaker.medialibrary.data.repository.PlayListsRepositoryImpl
import com.myproject.playlistmaker.medialibrary.data.shared_pref.SharedPrefDataSourceImpl
import com.myproject.playlistmaker.medialibrary.data.storage.InternalStorageClientImpl
import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksInteractor
import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListInteractor
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListsRepository
import com.myproject.playlistmaker.medialibrary.domain.usecase.FavoriteTracksInteractorImpl
import com.myproject.playlistmaker.medialibrary.domain.usecase.PlayListInteractorImpl
import com.myproject.playlistmaker.medialibrary.viewmodel.AddNewPlayListViewModel
import com.myproject.playlistmaker.medialibrary.viewmodel.FavoriteTracksViewModel
import com.myproject.playlistmaker.medialibrary.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavoriteTracksViewModel(androidContext() as App, get())
    }

    viewModel {
        PlaylistsViewModel(androidContext() as App, get())
    }

    viewModel {
        AddNewPlayListViewModel(get())
    }

    factory { TrackDbConvertor() }

    factory { PlayListDbConvertor() }

    factory { PlaylistTrackConverter() }

    single<FavoriteSharedPrefDataSource> {
        SharedPrefDataSourceImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get(), get())
    }

    single<PlayListsRepository> {
        PlayListsRepositoryImpl(get(), get(), get(), get())
    }

    single<InternalStorageClient> {
        InternalStorageClientImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }
    single<PlayListInteractor> {
        PlayListInteractorImpl(get())
    }
}