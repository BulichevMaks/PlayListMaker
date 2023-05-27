package com.myproject.playlistmaker.domain.api


import com.myproject.playlistmaker.domain.models.Track
import com.myproject.playlistmaker.domain.models.TracksResult

interface TrackRepository {
    fun getTracksByName(trackName: String, callback: (TracksResult) -> Unit)
    fun saveTrackToSharedPref(track: Track)
    fun getTrackFromSharedPref(): Track
}