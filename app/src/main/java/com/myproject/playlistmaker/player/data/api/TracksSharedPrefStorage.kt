package com.myproject.playlistmaker.player.data.api

import com.myproject.playlistmaker.player.data.model.TrackDataSource


interface TracksSharedPrefStorage {
    fun save(track: TrackDataSource)
    fun get(): TrackDataSource
    fun getTracksFromPref(): ArrayList<TrackDataSource>?

    fun writeTracksToPref(tracks: ArrayList<TrackDataSource>)
}