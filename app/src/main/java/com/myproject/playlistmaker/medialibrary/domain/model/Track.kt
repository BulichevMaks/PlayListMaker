package com.myproject.playlistmaker.medialibrary.domain.model

import java.io.Serializable

data class Track(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String?,
    val artworkUrl100: String,
    val previewUrl: String?,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
) : Serializable
