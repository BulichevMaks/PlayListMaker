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
    private var favoriteEnabled = false

    private var _playerTimingLiveData = MutableLiveData<String>()
    val playerTimingLiveData: LiveData<String> = _playerTimingLiveData


    private var _playerStateLiveData = MutableLiveData<Boolean>()
    val playerStateLiveData: LiveData<Boolean> = _playerStateLiveData


    private var _favoriteButtonStateLiveData = MutableLiveData<Boolean>()
    val favoriteButtonStateLiveData: LiveData<Boolean> = _favoriteButtonStateLiveData

    fun getTrack(): Track {
        val track = playerInteractor.getTrack()
        viewModelScope.launch {
            if (playerInteractor.isTrackFavorite(trackId = track.trackId)) {
                _favoriteButtonStateLiveData.value = true
                favoriteEnabled = true
            }
        }
        return track
    }

    public override fun onCleared() {
        playerInteractor.onDestroy()
        super.onCleared()
        timerJob?.cancel()
    }

    fun favoriteButtonControl() {
        if(favoriteEnabled) {
            _favoriteButtonStateLiveData.value = false
            favoriteEnabled = false
            viewModelScope.launch {
                playerInteractor.deleteById(playerInteractor.getTrack().trackId)
            }
        } else {
            _favoriteButtonStateLiveData.value = true
            favoriteEnabled = true
            viewModelScope.launch {
                playerInteractor.saveTrackToDB()
            }
        }
    }

    fun playHandlerControl() {
        playerInteractor.playerControl()
        _playerStateLiveData.value = playerInteractor.isPlayerPlaying()

        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlayerPlaying()) {
                delay(REFRESH_LIST_DELAY_MILLIS)
                _playerTimingLiveData.postValue(playerInteractor.getTiming())
                _playerStateLiveData.postValue(playerInteractor.isPlayerPlaying())
            }

        }

        playerInteractor.playerCompletion {
            timerJob?.cancel()
            _playerTimingLiveData.value = "00:00"
            _playerStateLiveData.value = false
        }

    }

    companion object {
        private const val REFRESH_LIST_DELAY_MILLIS = 300L
    }

}