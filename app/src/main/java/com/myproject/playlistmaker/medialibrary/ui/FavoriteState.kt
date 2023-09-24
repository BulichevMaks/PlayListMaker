package com.myproject.playlistmaker.medialibrary.ui

import com.myproject.playlistmaker.search.domain.model.Track


sealed interface FavoriteState {

    object Loading: FavoriteState
    data class Content(
        val tracks: List<Track>
    ): FavoriteState

    data class Empty(
        val message: String
    ): FavoriteState
}