package com.myproject.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.playlistmaker.player.domain.api.PlayerInteractor
import com.myproject.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    init {
        playerInteractor.preparePlayer()
    }

    private var timerJob: Job? = null

    var playerTimingLiveData = MutableLiveData<String>()
    fun observeTimingLiveData(): LiveData<String> = playerTimingLiveData

    var playerStateLiveData = MutableLiveData<Boolean>()

    fun observeStateLiveData(): LiveData<Boolean> = playerStateLiveData

    fun getTrack(): Track {
        return playerInteractor.getTrack()
    }

    public override fun onCleared() {
        playerInteractor.onDestroy()
        super.onCleared()
        timerJob?.cancel()
    }

    fun playHandlerControl() {
        playerInteractor.playerControl()
        playerStateLiveData.value = playerInteractor.isPlayerPlaying()

        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlayerPlaying()) {
                delay(REFRESH_LIST_DELAY_MILLIS)
                playerTimingLiveData.postValue(playerInteractor.getTiming())
                playerStateLiveData.postValue(playerInteractor.isPlayerPlaying())
            }

        }

        playerInteractor.playerCompletion {
            timerJob?.cancel()
            playerTimingLiveData.value = "00:00"
            playerStateLiveData.value = false
        }

    }

    companion object {
        private const val REFRESH_LIST_DELAY_MILLIS = 300L
    }

}