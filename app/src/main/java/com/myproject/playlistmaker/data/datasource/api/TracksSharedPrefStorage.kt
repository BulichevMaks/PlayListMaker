package com.myproject.playlistmaker.data.datasource.api

import com.myproject.playlistmaker.data.datasource.models.TrackDataSource

interface TracksSharedPrefStorage {
    fun save(track: TrackDataSource)
    fun get(): TrackDataSource
}