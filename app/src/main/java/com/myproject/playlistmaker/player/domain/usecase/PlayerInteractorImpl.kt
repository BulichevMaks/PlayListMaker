package com.myproject.playlistmaker.player.domain.usecase

import com.myproject.playlistmaker.player.domain.api.PlayerApi
import com.myproject.playlistmaker.player.domain.api.PlayerInteractor
import com.myproject.playlistmaker.player.domain.api.PlayerRepository
import com.myproject.playlistmaker.player.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerInteractorImpl(
    private val playerApi: PlayerApi,
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun getTrack(): Track {
        return playerRepository.getTrackFromSharedPref()
    }

    override fun preparePlayer() {
        playerApi.preparePlayer(playerRepository.getTrackFromSharedPref())
    }

    override fun pausePlayer() {
        playerApi.pausePlayer()
    }

    override fun onDestroy() {
        playerApi.onDestroy()
    }

    override fun playerControl() {
        playerApi.playbackControl()
    }

    override fun isPlayerPlaying(): Boolean {
        return playerApi.isPlayerPlaying()
    }

    override fun getTiming(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerApi.getCurrentPosition())
    }

    override fun playerCompletion(onComplete: () -> Unit) {
        playerApi.setOnCompletionListener {
            onComplete()
        }
    }
}