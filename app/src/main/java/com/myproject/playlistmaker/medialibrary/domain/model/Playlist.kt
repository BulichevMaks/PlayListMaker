package com.myproject.playlistmaker.medialibrary.domain.model

data class Playlist(
    val name: String,
    val tracksAmount: Int,
    val tracks: ArrayList<Track>,
)
