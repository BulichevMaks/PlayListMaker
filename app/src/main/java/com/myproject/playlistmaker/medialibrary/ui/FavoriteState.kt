package com.myproject.playlistmaker.medialibrary.ui

import com.myproject.playlistmaker.medialibrary.domain.model.Track

sealed interface FavoriteState {
    data class Content(
        val tracks: List<Track>
    ) : FavoriteState

    data class Empty(
        val message: String
    ) : FavoriteState
}