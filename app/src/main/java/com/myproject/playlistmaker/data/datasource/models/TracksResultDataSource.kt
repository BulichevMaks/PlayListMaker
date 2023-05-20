package com.myproject.playlistmaker.data.datasource.models

sealed class TracksResultDataSource {
    data class Success(val tracks: ArrayList<TrackDataSource>) : TracksResultDataSource()
    data class Error(val code: Int) : TracksResultDataSource()
}