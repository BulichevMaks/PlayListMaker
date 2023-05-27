package com.myproject.playlistmaker.data.datasource.api

import com.myproject.playlistmaker.data.datasource.models.TracksResultDataSource

interface TracksNetworkStorage {
    fun getTracksByName(trackName: String, callback: (TracksResultDataSource) -> Unit)
}