package com.myproject.playlistmaker.di

import com.myproject.playlistmaker.search.data.api.NetworkClient
import com.myproject.playlistmaker.search.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.search.data.network.RetrofitNetworkClientImpl
import com.myproject.playlistmaker.search.data.network.TrackApi
import com.myproject.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.myproject.playlistmaker.search.data.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.usecase.ClearHistoryUseCase
import com.myproject.playlistmaker.search.domain.usecase.ItemClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.ItemHistoryClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.myproject.playlistmaker.search.domain.usecase.ShowHistoryUseCase
import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import java.util.concurrent.Executors
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITUNES_BASE_URL = "https://itunes.apple.com"

val searchModule = module {

    single<TrackApi> {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
            .build()
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClientImpl(get(), androidContext())
    }

    single<TracksSharedPrefStorage> {
        TracksSharedPrefDataSourceImpl(get(), get())
    }

    factory<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single {
        ItemClickUseCase(get())
    }

    single {
        ItemHistoryClickUseCase(get())
    }

    single {
        Executors.newCachedThreadPool()
    }

    single {
        SearchTracksUseCase(get(), get())
    }

    single {
        ShowHistoryUseCase(get())
    }

    single {
        ClearHistoryUseCase(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get(), get(), get(), get())
    }

}
