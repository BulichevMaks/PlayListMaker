package com.myproject.playlistmaker.search.domain.api

import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.domain.madel.SearchResult


interface SearchRepository {
    fun getTracksByName(trackName: String): SearchResult<List<Track>>
    fun saveTrackToSharedPref(track: Track)
    fun getTrackFromSharedPref(): Track
    fun readTracksFromSharedPref(): ArrayList<Track>?
    fun writeTracksToPref(tracks: ArrayList<Track>)
}