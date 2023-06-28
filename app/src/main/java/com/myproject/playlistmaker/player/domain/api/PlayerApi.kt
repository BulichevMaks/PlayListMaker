package com.myproject.playlistmaker.player.domain.api

import com.myproject.playlistmaker.player.domain.model.Track


interface PlayerApi {

    fun preparePlayer(track: Track)

    fun startPlayer()

    fun pausePlayer()

    fun onDestroy()

    fun getCurrentPosition(): Int

    fun playbackControl()

    fun getCurrentState(): Int

    fun isPlayerPlaying(): Boolean

    fun setOnCompletionListener(onComplete: () -> Unit)
}