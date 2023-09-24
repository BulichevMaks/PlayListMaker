package com.myproject.playlistmaker.player.data

import android.media.MediaPlayer
import com.myproject.playlistmaker.player.domain.api.PlayerApi
import com.myproject.playlistmaker.search.domain.model.Track

class PlayerHandler(private val mediaPlayer: MediaPlayer): PlayerApi {

    private var playerState = STATE_DEFAULT
    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
    }
    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }
    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }
    override fun onDestroy() {
        playerState = STATE_PREPARED
        mediaPlayer.release()
    }
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    override fun getCurrentState(): Int {
        return playerState
    }

    override fun isPlayerPlaying(): Boolean {
       return getCurrentState() == STATE_PLAYING
    }

    override fun setOnCompletionListener(onComplete: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onComplete()
            playerState = STATE_PREPARED
        }
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
