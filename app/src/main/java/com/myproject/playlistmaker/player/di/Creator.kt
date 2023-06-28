package com.myproject.playlistmaker.player.di

import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.player.domain.api.PlayerApi
import com.myproject.playlistmaker.player.data.PlayerHandler
import com.myproject.playlistmaker.player.data.datasource.PlayerSharedPrefDataSourceImpl
import com.myproject.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.myproject.playlistmaker.player.domain.usecase.PlayerInteractorImpl


object Creator {
    fun getPlayer(): PlayerApi {
        return PlayerHandler()
    }

    fun getPlayerInteractor(): PlayerInteractorImpl {

        val tracksSharedPreferences = PlayerSharedPrefDataSourceImpl(context = App.getInstance().applicationContext)
        val playerRepository = PlayerRepositoryImpl(tracksSharedPreferences)

        return PlayerInteractorImpl( getPlayer(), playerRepository)
    }
}