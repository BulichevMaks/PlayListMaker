package com.myproject.playlistmaker.domain.models

sealed class TracksResult {
    data class Success(val tracks: ArrayList<Track>) : TracksResult()
    data class Error(val code: Int) : TracksResult()
}
