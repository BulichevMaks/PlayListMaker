package com.myproject.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myproject.playlistmaker.player.domain.api.PlayerInteractor
import com.myproject.playlistmaker.player.domain.model.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    init {
        playerInteractor.preparePlayer()
    }

    private var mainThreadHandler: Handler? = null
    var playerTimingLiveData = MutableLiveData<String>()

    fun observeTimingLiveData(): LiveData<String> = playerTimingLiveData

    var playerStateLiveData = MutableLiveData<Boolean>()

    fun observeStateLiveData(): LiveData<Boolean> = playerStateLiveData

    fun getTrack(): Track {
        return playerInteractor.getTrack()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStateLiveData.value = false
    }

    fun onDestroy() {
        playerInteractor.onDestroy()
    }

    fun playHandlerControl() {
        mainThreadHandler = Handler(Looper.getMainLooper())
        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerInteractor.isPlayerPlaying()) {
                        playerTimingLiveData.value = playerInteractor.getTiming()
                        playerStateLiveData.value = playerInteractor.isPlayerPlaying()
                        mainThreadHandler?.postDelayed(
                            this,
                            REFRESH_LIST_DELAY_MILLIS,
                        )
                    }
                }
            },
            REFRESH_LIST_DELAY_MILLIS
        )

        playerInteractor.playerControl()
        playerStateLiveData.value = playerInteractor.isPlayerPlaying()

        playerInteractor.playerCompletion {
            playerTimingLiveData.value = "00:00"
            playerStateLiveData.value = false
        }

    }

    companion object {
        private const val REFRESH_LIST_DELAY_MILLIS = 500L
    }

}