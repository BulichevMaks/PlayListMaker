package com.myproject.playlistmaker.search.ui.models

import com.myproject.playlistmaker.search.domain.model.Track


sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class ServerError(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState

}