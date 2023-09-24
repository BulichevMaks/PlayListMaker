package com.myproject.playlistmaker.medialibrary.data.api

import com.myproject.playlistmaker.medialibrary.data.model.TrackDataSource

interface FavoriteSharedPrefDataSource {
    fun save(track: TrackDataSource)
}