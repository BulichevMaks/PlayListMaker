package com.myproject.playlistmaker.data.playerdata

import com.myproject.playlistmaker.domain.models.Track

interface PlayerApi {

    fun preparePlayer(track: Track)

    fun startPlayer()

    fun pausePlayer()

    fun onDestroy()

    fun getCurrentPosition(): Int

    fun playbackControl(play: () -> Unit, pause: () -> Unit)

    fun getCurrentState(): Int

    fun setOnCompletionListener(onComplete: () -> Unit)
}