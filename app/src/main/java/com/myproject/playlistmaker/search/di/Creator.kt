package com.myproject.playlistmaker.search.di

import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.myproject.playlistmaker.search.data.network.RetrofitNetworkClientImpl
import com.myproject.playlistmaker.search.data.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.search.domain.api.SearchRepository


object Creator {
    fun getSearchRepository(): SearchRepository {
        val tracksNetworkDataSourceImpl =
            RetrofitNetworkClientImpl(context = App.getInstance().applicationContext)

        val tracksSharedPreferences = TracksSharedPrefDataSourceImpl(context = App.getInstance().applicationContext)

        return SearchRepositoryImpl(tracksNetworkDataSourceImpl, tracksSharedPreferences)
    }
}