package com.myproject.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myproject.playlistmaker.player.di.Creator
import com.myproject.playlistmaker.player.domain.api.PlayerInteractor

class PlayerViewModelFactory : ViewModelProvider.Factory {

    private val playerInteractor by lazy {
        Creator.getPlayerInteractor()
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(playerInteractor) as T
    }
}