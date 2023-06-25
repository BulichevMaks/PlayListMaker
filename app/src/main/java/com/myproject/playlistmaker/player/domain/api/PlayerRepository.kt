package com.myproject.playlistmaker.player.domain.api

import com.myproject.playlistmaker.player.domain.model.Track


interface PlayerRepository {
    fun getTrackFromSharedPref(): Track
}