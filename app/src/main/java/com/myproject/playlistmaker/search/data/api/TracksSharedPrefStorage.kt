package com.myproject.playlistmaker.search.data.api

import com.myproject.playlistmaker.search.data.model.TrackDataSource
import com.myproject.playlistmaker.search.domain.madel.Track


interface TracksSharedPrefStorage {
    fun save(track: TrackDataSource)
    fun get(): TrackDataSource
    fun getTracksFromPref(): ArrayList<Track>?

    fun writeTracksToPref(tracks: ArrayList<TrackDataSource>)
}