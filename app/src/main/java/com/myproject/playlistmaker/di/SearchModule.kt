package com.myproject.playlistmaker.di

import android.content.Context
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.myproject.playlistmaker.search.data.api.NetworkClient
import com.myproject.playlistmaker.search.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.search.data.network.RetrofitNetworkClientImpl
import com.myproject.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.myproject.playlistmaker.search.data.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.usecase.*
import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {

//    single {
//        androidContext()
//    }

    single<NetworkClient> {
        RetrofitNetworkClientImpl(get())
    }

    single<TracksSharedPrefStorage> {
        TracksSharedPrefDataSourceImpl(get(),get())
    }

    factory <SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single {
        ItemClickUseCase(get())
    }
    single {
        ItemHistoryClickUseCase(get())
    }
    single {
        SearchTracksUseCase(get())
    }
    single {
        ShowHistoryUseCase(get())
    }
    single {
        StopActivityUseCase(get())
    }

    viewModel {
        SearchViewModel(get(),get(),get(),get(),get(),get())
    }

}
//fun getSearchRepository(): SearchRepository {
//    val tracksNetworkDataSourceImpl =
//        RetrofitNetworkClientImpl(context = App.getInstance().applicationContext)
//
//    val tracksSharedPreferences = TracksSharedPrefDataSourceImpl(context = App.getInstance().applicationContext)
//
//    return SearchRepositoryImpl(tracksNetworkDataSourceImpl, tracksSharedPreferences)
//}