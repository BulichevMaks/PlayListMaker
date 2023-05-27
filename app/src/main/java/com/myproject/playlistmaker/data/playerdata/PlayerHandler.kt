package com.myproject.playlistmaker.data.playerdata

import android.media.MediaPlayer
import com.myproject.playlistmaker.domain.models.Track

class PlayerHandler: PlayerApi {
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
   // private var url = track.previewUrl

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

    override fun playbackControl(play: () -> Unit, pause: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                pause()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                play()
            }
        }
    }

    override fun getCurrentState(): Int {
        return playerState
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
