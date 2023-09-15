package com.myproject.playlistmaker.medialibrary.domain.model

import android.net.Uri
import com.myproject.playlistmaker.search.domain.model.Track

data class Playlist(
    val id: Int = 0,
    val image: Uri?,
    val name: String,
    val description: String?,
    var trackCount: Int = 0,
    val tracks: MutableList<Track> = mutableListOf()
)
