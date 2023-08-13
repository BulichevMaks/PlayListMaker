package com.myproject.playlistmaker.di

import android.media.MediaPlayer
import com.google.gson.Gson
import com.myproject.playlistmaker.player.data.PlayerHandler
import com.myproject.playlistmaker.player.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.player.data.datasource.PlayerSharedPrefDataSourceImpl
import com.myproject.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.myproject.playlistmaker.player.domain.api.PlayerApi
import com.myproject.playlistmaker.player.domain.api.PlayerInteractor
import com.myproject.playlistmaker.player.domain.api.PlayerRepository
import com.myproject.playlistmaker.player.domain.usecase.PlayerInteractorImpl
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { Gson() }

    single<TracksSharedPrefStorage> {
        PlayerSharedPrefDataSourceImpl(get(), get())
    }

    factory {
        MediaPlayer()
    }

    factory<PlayerApi> {
        PlayerHandler(get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get(), get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get(), get())
    }
}