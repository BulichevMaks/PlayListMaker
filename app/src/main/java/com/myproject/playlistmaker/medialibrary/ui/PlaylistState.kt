package com.myproject.playlistmaker.medialibrary.ui

import com.myproject.playlistmaker.medialibrary.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    data class Empty(
        val message: String
    ) : PlaylistState
}