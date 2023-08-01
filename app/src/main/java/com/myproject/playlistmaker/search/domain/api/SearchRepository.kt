package com.myproject.playlistmaker.search.domain.api

import com.myproject.playlistmaker.search.domain.model.Track
import com.myproject.playlistmaker.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow


interface SearchRepository {
    fun getTracksByName(trackName: String): Flow<SearchResult<List<Track>>>
    fun saveTrackToSharedPref(track: Track)
    fun getTrackFromSharedPref(): Track
    fun readTracksFromSharedPref(): ArrayList<Track>?
    fun writeTracksToPref(tracks: ArrayList<Track>)
    fun clearHistory()
}